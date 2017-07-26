package org.bdc.dcm.data.coder.lc.decoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.lc.CommandTypeCtr;
import org.bdc.dcm.data.coder.lc.util.LcComonUtils;
import org.bdc.dcm.data.coder.lc.vo.CommLcParam;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.netty.TcpClient;
import org.bdc.dcm.netty.lc.LcTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

/**
 * 8byte mac
 * 1byte 序号
 * Xbyte modbus
 * @author Administrator
 *
 */
public class CmdDecoder_17H implements CommandTypeCtr {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final int macLen = 8;
	
	private final DataTabConf dataTabConf;
	
	public CmdDecoder_17H() {
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	
	@Override
	public DataPack mapTo(CommLcParam param) {
		DataPack dataPack;
		List<DataTab> dataTabList = dataTabConf.getDataTabConf("Lc");
		
		ByteBuf in = param.getPack();
		
		byte[] s = new byte[in.readableBytes()];
		in.getBytes(0, s);
		logger.info("mapTo:{}",Public.byte2hex(s));
		
		byte[] mac = new byte[macLen];
		in.readBytes(mac);
		dataPack = LcComonUtils.getInitDataPack(Public.byte2hex(mac));
		dataPack.setData(new HashMap<>());
		//拿掉 序号
		in.readByte();
		
		//modbus 数据长度
		in.markReaderIndex();
		byte[] modbusHeader = new byte[3];
		in.readBytes(modbusHeader);//modbus 地址和功能码
		int modbusDataLen = modbusHeader[2] & 0xff;
		//in 还处于完整 modbus
		in.resetReaderIndex();
		
		//------------------modebus------------------------------------------------------
		//modbus 完整包
		byte[] modebus = new byte[3 + modbusDataLen + 2];
		//刚好抵消 序号的位置
		in.readBytes(modebus);
		in.resetReaderIndex();
		//------------------modebus 数据+crc------------------------------------------------------
		in.readByte();//地址
		byte funcode = in.readByte();
		if(funcode == (byte)0x03) {
			in.readByte();//长度
			ByteBuf modbusDataBuf = in.readBytes(modbusDataLen + 2);
		
			dataPack = LcComonUtils.getInitDataPack(Public.byte2hex(mac) + " " +Public.byte2hex_ex(modbusHeader[0]));
			Map<String, Object> data = new HashMap<String, Object>();
			for(int i=128;i<(modbusDataLen/2 + 128);i++){
				List<Object> val = LcComonUtils.decodeValue(i, modbusDataBuf, dataTabList);
				Object o = val.get(1);
				if(o != null)
					data.put(i + "", val);
			}
			
			dataPack.setData(data);
			return dataPack;
		}else if(funcode == (byte)0x05) {
			int i = in.readInt();
			Map<String, Object> data = new HashMap<>();
			List<Object> list = new ArrayList<>();
			list.add("");
			list.add(i>0);
			data.put(LcTypeConvert.DATATYPE_JDQSTATE+"", list);
			dataPack.setData(data);
			in.readShort();//读完
			return dataPack;
		}else {
			byte[] bs = new byte[in.readableBytes()];
			in.readBytes(bs);
		}
		return dataPack;
	}

}
