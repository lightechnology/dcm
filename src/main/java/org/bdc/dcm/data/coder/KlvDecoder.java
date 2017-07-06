package org.bdc.dcm.data.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.netty.klv.KlvTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataPackType;

import com.util.tools.Public;

public class KlvDecoder implements DataDecoder<ByteBuf> {
	
	private final DataTabConf dataTabConf;

	public KlvDecoder() {
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	
	// 通过DataTypeConf接口获取解码规则
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, ByteBuf msg) {
		msg.markReaderIndex();
		List<DataTab> dataTabList = dataTabConf.getDataTabConf("klv");
		Map<String, Object> data = new HashMap<String, Object>();
		ByteBuf byteBuf = msg.slice(3, msg.readableBytes() - 6);
		data.put("-1", makeMapValue("onlineStatus", 1));
		data.put("0", makeMapValue("dataPackType", byteBuf.readByte() & 0xFF));
		while (byteBuf.isReadable()) {
			int id = byteBuf.readByte() & 0xFF;
			int length = byteBuf.readByte() & 0xFF;
			Object value = decodeValue(id, byteBuf.readBytes(length), dataTabList);
			data.put(id + "", value);
		}
		msg.resetReaderIndex();
		return buildDataPack(data);
	}
	
	private List<Object> decodeValue(int id, ByteBuf value,
			List<DataTab> dataTabList) {
		for (int i = 0; i < dataTabList.size(); i++) {
			DataTab dataTab = dataTabList.get(i);
			try {
			if (id == dataTab.getId()) {
				return makeMapValue(dataTab.getName(),
						KlvTypeConvert.convertByteBuf2TypeValue(
								dataTab.getForm(), value));
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		byte data[] = new byte[value.readableBytes()];
		value.readBytes(data);
		return makeMapValue("", Public.byte2hex(data));
	}
	
	private List<Object> makeMapValue(String name, Object value) {
		List<Object> vl = new ArrayList<Object>();
		vl.add(name);
		vl.add(value);
		return vl;
	}
	
	private Object getMapValue(Map<String, Object> map, String key) {
		@SuppressWarnings("unchecked")
		List<Object> vl = (List<Object>) map.get(key);
		if (1 < vl.size())
			return vl.get(1);
		return null;
	}
	
	private DataPack buildDataPack(Map<String, Object> data) {
		DataPack dataPack = new DataPack();
		dataPack.setData(data);
		if (data.containsKey("-1"))
			dataPack.setOnlineStatus(Public.objToInt(getMapValue(data, "-1")));
		if (data.containsKey("0"))
			dataPack.setDataPackType(DataPackType.values()[Public.objToInt(getMapValue(data, "0"))]);
		if (data.containsKey("98"))
			dataPack.setToMac(Public.objToStr(getMapValue(data, "98")));
		if (data.containsKey("101"))
			dataPack.setToAddr(Public.objToStr(getMapValue(data, "101")));
		if (data.containsKey("102"))
			dataPack.setAddr(Public.objToStr(getMapValue(data, "102")));
		if (data.containsKey("103"))
			dataPack.setMac(Public.objToStr(getMapValue(data, "103")));
		if (data.containsKey("105"))
			dataPack.setParentMac(Public.objToStr(getMapValue(data, "105")));
		if (data.containsKey("106"))
			dataPack.setDeviceType(Public.objToInt(getMapValue(data, "106")));
		if (data.containsKey("112"))
			dataPack.setBackSign(Public.objToStr(getMapValue(data, "112")));
		if (data.containsKey("120"))
			dataPack.setDataPackName(Public.objToStr(getMapValue(data, "120")));
		return dataPack;
	}
	
}
