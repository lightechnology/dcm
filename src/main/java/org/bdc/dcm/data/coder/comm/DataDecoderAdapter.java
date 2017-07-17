package org.bdc.dcm.data.coder.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.utils.CommTypeConvert;
import org.bdc.dcm.vo.DataModel;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public abstract class DataDecoderAdapter implements DataDecoder<ByteBuf>{

	/**
	 * 寄存器表
	 */
	private List<DataTab> regTable;
	
	private CommTypeConvert convert ;
	
	private DataType dataType;
	
	public DataDecoderAdapter(DataType dataType) {
		try {
			this.regTable = IntfConf.getDataTabConf().getDataTabConf(dataType.name());
			this.convert = (CommTypeConvert)Class.forName("org.bdc.dcm.utils."+dataType.name()+"TypeConvert").newInstance();
			this.dataType = dataType;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<DataTab> getRegTable() {
		return regTable;
	}

	// 通过DataTypeConf接口获取解码规则
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, ByteBuf msg) {
		Map<String,DataModel> map = new HashMap<>();
		Map<String,Object> tmpData = new HashMap<>();
		//整包配置
		for(DataTab tab:regTable){
			if(!CommTypeConvert.getTypeToMethodInRW().containsKey(tab.getForm())){
				customProtocol(ctx,msg,tmpData);
			}else{
				Object o = convert.readAndConvert(tab,dataType, msg);
				DataModel dataModel = DataModel.mapBy(tab, o);
				map.put(dataModel.getId()+"", dataModel);
			}
		}
		return knownProtocol(ctx,map,tmpData);
	}
	/**
	 * 
	 * @param ctx
	 * @param msg 解析不出来的数据
	 * @param tmpData 存放传递的变量区域
	 */
	public abstract void customProtocol(ChannelHandlerContext ctx, ByteBuf msg,Map<String,Object> tmpData);
	/**
	 * 
	 * @param ctx
	 * @param map
	 * @param tmpData 存放传递的变量区域
	 * @return
	 */
	public abstract DataPack knownProtocol(ChannelHandlerContext ctx, Map<String,DataModel> map,Map<String,Object> tmpData);
}
