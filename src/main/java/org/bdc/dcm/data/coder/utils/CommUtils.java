package org.bdc.dcm.data.coder.utils;

import static org.bdc.dcm.data.coder.utils.CommUtils.decodeValue;
import static org.bdc.dcm.data.coder.utils.CommUtils.getInitInfoDataPack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.data.coder.intf.TypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.Datalevel;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class CommUtils {

	public enum ModusResp{
		address,funcode,len
	}
	
	public static List<Object> makeMapValue(String name, Object value) {
		List<Object> vl = new ArrayList<Object>();
		vl.add(name);
		vl.add(value);
		return vl;
	}
	public static DataPack getInitInfoDataPack(String mac) {
		DataPack dataPack = new DataPack();
		dataPack.setMac(mac);
		dataPack.setOnlineStatus(1);
		dataPack.setDatalevel(Datalevel.NORMAL);
		dataPack.setDataPackType(DataPackType.Info);
		return dataPack;
	}
	public static DataPack getInitCmdDataPack(String toMac) {
		DataPack dataPack = new DataPack();
		dataPack.setToMac(toMac);
		dataPack.setOnlineStatus(1);
		dataPack.setDatalevel(Datalevel.NORMAL);
		dataPack.setDataPackType(DataPackType.Cmd);
		return dataPack;
	}
	public static String gernatorIndetity(byte[] macBytes,byte address){
		String mac = "";
		for(int i = 0;i < macBytes.length;i++)
			mac += Public.byte2hex_ex(macBytes[i]);
		mac += Public.byte2hex_ex(address);
		return mac;
	}
	public static List<Object> decodeValue(int id, ByteBuf value,List<DataTab> dataTabList,TypeConvert convert) {
		for (int i = 0; i < dataTabList.size(); i++) {
			DataTab dataTab = dataTabList.get(i);
			try {
				if (id == dataTab.getId()) {
					return makeMapValue(dataTab.getName(),convert.decode(dataTab.getForm(), value));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		byte data[] = new byte[value.readableBytes()];
		value.readBytes(data);
		return makeMapValue("", Public.byte2hex(data));
	}
	public static DataPack funCode_UnKnow(ByteBuf in, byte[] mac, byte addr) {
		byte[] bs = new byte[in.readableBytes()];
		in.readBytes(bs);
		DataPack dataPack = getInitInfoDataPack(Public.byte2hex(mac)+ " " +Public.byte2hex_ex(addr));
		dataPack.setData(new HashMap<>());
		return dataPack;
	}

	public static DataPack funCode_03(List<DataTab> dataTabList, ByteBuf in, byte[] mac, int baseRegAddr, TypeConvert convert,
			byte addr) {
		int modbusDataLen = in.readByte()&0xff;//长度
		ByteBuf modbusDataBuf = in.readBytes(modbusDataLen);

		DataPack dataPack = getInitInfoDataPack(Public.byte2hex(mac) + " " +Public.byte2hex_ex(addr));
		Map<String, Object> data = new HashMap<String, Object>();
		for(int i=baseRegAddr;modbusDataBuf.isReadable();i++){
			List<Object> val = decodeValue(i, modbusDataBuf, dataTabList,convert);
			Object o = val.get(1);
			if(o != null)
				data.put(i + "", val);
		}
		
		dataPack.setData(data);
		in.readShort();//读完
		return dataPack;
	}

}
