package org.bdc.dcm.vo.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bdc.dcm.utils.BaseTypeToBytesUtils;
import org.bdc.dcm.utils.CommTypeConvert;
import org.bdc.dcm.vo.DataModel;
import org.bdc.dcm.vo.DataTab;

import io.netty.buffer.ByteBuf;

public  class ModbusProtocol {
	
	/**
	 * 寄存器地址
	 */
	private final List<Integer> regAddrs;
	
	private CommTypeConvert convert ;

	private BaseTypeToBytesUtils baseTypeToBytesUtils;
	
	public ModbusProtocol(CommTypeConvert convert,List<DataTab> regTable) {
		super();
		this.convert = convert;
		this.regAddrs = regTable.stream().map(item->item.getId()).collect(Collectors.toList());
	}
	
	protected boolean validateProtocol(byte modbusAddr,ModbusFunCode code,List<DataModel> dataModelList){
		if(modbusAddr < 1 || code ==null || dataModelList ==null ) return false;
		else return true;
	}
	/**
	 * 支持 同一连续地址寄存器 发送
	 * @see BaseTypeToBytesUtils
	 * @param 发送rw请求  下行数据
	 * </br>读：从机地址+功能码+寄存器起始地址高字节+寄存器起始地址低字节+{readReq}+crc校验
	 * </br>写：从机地址+功能码+寄存器起始地址高字节+寄存器起始地址低字节+{writeReq}+crc校验
	 * @param code
	 * @param dataModelList 需要写的数据模型
	 * @param out
	 * @throws Exception 
	 */
	public void sendRequest(boolean rw,byte modbusAddr,ModbusFunCode code,List<DataModel> dataModelList,ByteBuf out) throws Exception{
		boolean flag = validateProtocol(modbusAddr, code, dataModelList);
		if(!flag) return ;
		List<DataModel> writeRegTable = dataModelList.stream().filter(item->regAddrs.contains(item.getId())).collect(Collectors.toList());
		//保证地址连续性 不会被过滤
		if(writeRegTable.size() == 0 ||	writeRegTable.size() != dataModelList.size())return ;
		
		writeRegTable.set(0, new DataModel(modbusAddr, "byte"));
		writeRegTable.set(1, new DataModel(code.getCode(),"byte"));
		
		byte[] regFirstAddr = BaseTypeToBytesUtils.intToBytes(writeRegTable.get(0).getId());
		byte[] regLen = BaseTypeToBytesUtils.intToBytes(writeRegTable.size());
		
		writeRegTable.set(2, new DataModel(regFirstAddr[0], "byte"));
		writeRegTable.set(3, new DataModel(regFirstAddr[1], "byte"));
		
		if(rw){
			readReq(writeRegTable,regLen);
		}else{
			writeReq(writeRegTable,regLen);
		}
		
		ByteArrayOutputStream io = new ByteArrayOutputStream();
		for(DataModel model:writeRegTable){
			Object val = model.getVal();
			String simpleName = convert.paramMapToBaseTypeSimpleName(val);
			Class<?> clazz = convert.paramMapToBaseClass(val);
			Method m = baseTypeToBytesUtils.getClass().getMethod(simpleName+"ToBytes", clazz);
			byte[] bytes = (byte[])m.invoke(baseTypeToBytesUtils, val);
			//TODO 查看需不需要数组翻转
			io.write(bytes);	
		}
		byte[] bs = io.toByteArray();
		byte[] crc16 = Public.crc16_A001(bs);
		io.close();
		
		writeRegTable.add(new DataModel(crc16[1], "byte"));
		writeRegTable.add(new DataModel(crc16[0], "byte"));
		convert.write(writeRegTable, out);
	}
	
	/**
	 * 写寄存器个数+写入的数据
	 * @param writeRegTable
	 * @param out
	 * @return 返回两字节 crc
	 * @throws Exception 
	 */
	private void writeReq(List<DataModel> writeRegTable,byte[] regLen) throws Exception{
		writeRegTable.set(4, new DataModel(regLen[0], "byte"));
	}
	/**
	 * 读取寄存器个数高字节+读取寄存器个数低字节
	 * @param writeRegTable
	 * @param out
	 * @return 返回两字节 crc
	 * @throws Exception 
	 */
	private void readReq(List<DataModel> writeRegTable,byte[] regLen) throws Exception{
		
		writeRegTable.set(4, new DataModel(regLen[0], "byte"));
		writeRegTable.set(5, new DataModel(regLen[1], "byte"));
		writeRegTable = writeRegTable.subList(0, 5);//不需要数据位
	}
}
