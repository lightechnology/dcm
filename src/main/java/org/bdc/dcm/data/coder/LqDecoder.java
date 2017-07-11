package org.bdc.dcm.data.coder;

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
import org.bdc.dcm.vo.e.Datalevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class LqDecoder implements  DataDecoder<ByteBuf>  {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final DataTabConf dataTabConf;
	
	private int macLen = 6;
	
	private int modBusHeadLen = 3;
	
	private int crcLen = 2;
	
	private enum ModusResp{
		address,funcode,len
	}
	
	public LqDecoder() {
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	/**
	 * 能进来的都是可读的不用判断
	 * 数据id: 功能码+寄存器编号
	 * 数据值  : 寄存器值
	 */
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, ByteBuf msg){
		try {
			msg.markReaderIndex();
			List<DataTab> dataTabList = dataTabConf.getDataTabConf("Lq");
			
			byte[] macBytes = new byte[macLen];
			byte[] modBusHeadBytes = new byte[modBusHeadLen];
			byte[] crcBytes = new byte[crcLen];
			
			msg.readBytes(macBytes);
			msg.readBytes(modBusHeadBytes);
			int contentLen = modBusHeadBytes[ModusResp.len.ordinal()] & 0xff;
			Map<String, Object> data = new HashMap<String, Object>();
			//功能码
			data.put("12", makeMapValue("funCode", modBusHeadBytes[ModusResp.funcode.ordinal()]& 0xff));
			
			for(int i=0;i<contentLen/2;i++){
				ByteBuf buf = msg.readBytes(2);
				buf.markReaderIndex();
				byte[] a = new byte[2];
				buf.readBytes(a );
				if(logger.isDebugEnabled())
					logger.debug("设备：{},信息：{}", modBusHeadBytes[ModusResp.address.ordinal()]& 0xff,a);
				buf.resetReaderIndex();
				Object value = decodeValue(i, buf, dataTabList);
				data.put(i + "", value);
			}
			String indetity = gernatorIndetity(macBytes, modBusHeadBytes[1]);
			DataPack pack = getInitDataPack(indetity);
			pack.setData(data);
			msg.readBytes(crcBytes);
			
			return pack;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new DataPack();
	}
	//字段类型跟值
	private List<Object> makeMapValue(String name, Object value) {
		List<Object> vl = new ArrayList<Object>();
		vl.add(name);
		vl.add(value);
		return vl;
	}
	private DataPack getInitDataPack(String mac) {
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
	private String gernatorIndetity(byte[] macBytes,byte address){
		String mac = "";
		for(int i = 0;i < macBytes.length;i++)
			mac += Public.byte2hex_ex(macBytes[i]);
		mac += Public.byte2hex_ex(address);
		return mac;
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
}
