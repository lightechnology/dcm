package org.bdc.dcm.data.coder.lcmdb.utils;

import java.util.ArrayList;
import java.util.List;

import org.bdc.dcm.netty.lcmdb.LcmdbTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.Datalevel;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;


public class LcComonUtils {

	public static List<Object> makeMapValue(String name, Object value) {
		List<Object> vl = new ArrayList<Object>();
		vl.add(name);
		vl.add(value);
		return vl;
	}
	public static DataPack getInitDataPack(String mac) {
		DataPack dataPack = new DataPack();
		dataPack.setMac(mac);
		dataPack.setOnlineStatus(1);
		dataPack.setDatalevel(Datalevel.NORMAL);
		dataPack.setDataPackType(DataPackType.Info);
		return dataPack;
	}
	/**
	 * 
	 * @param mac
	 * @param address 设备总线地址
	 * @return
	 */
	public static String gernatorIndetity(byte[] macBytes,byte address){
		String mac = "";
		for(int i = 0;i < macBytes.length;i++)
			mac += Public.byte2hex_ex(macBytes[i]);
		mac += Public.byte2hex_ex(address);
		return mac;
	}
	/**
	 * 
	 * @param id
	 * @param value modbus data + crc
	 * @param dataTabList
	 * @return
	 */
	public static List<Object> decodeValue(int id, ByteBuf value,List<DataTab> dataTabList) {
		for (int i = 0; i < dataTabList.size(); i++) {
			DataTab dataTab = dataTabList.get(i);
			try {
				if (id == dataTab.getId()) {
					return makeMapValue(dataTab.getName(),LcmdbTypeConvert.getConvert().decode(dataTab.getForm(), value));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return makeMapValue("", null);
	}

}
