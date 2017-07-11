package org.bdc.dcm.data.coder.lc.decoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.lc.CommandTypeCtr;
import org.bdc.dcm.data.coder.lc.util.LcComonUtils;
import org.bdc.dcm.data.coder.lc.vo.CommLcParam;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;

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
	
	private static final int macLen = 8;
	
	private final DataTabConf dataTabConf;
	
	public CmdDecoder_17H() {
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	
	@Override
	public DataPack mapTo(CommLcParam param) {
		List<DataTab> dataTabList = dataTabConf.getDataTabConf("Lc");
		
		ByteBuf in = param.getPack();
		//为了日志
		in.markReaderIndex();
		byte[] packByte = new byte[in.readableBytes()];
		in.readBytes(packByte);
		in.resetReaderIndex();
		
		byte[] mac = new byte[macLen];
		in.readBytes(mac);
		
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
		in.readBytes(3);//偏移 地址 + 功能码 + 长度标识
		ByteBuf modbusDataBuf = in.readBytes(modbusDataLen + 2);
	
		DataPack datePack = LcComonUtils.getInitDataPack(Public.byte2hex(mac) + " " +Public.byte2hex_ex(modbusHeader[0]));
		Map<String, Object> data = new HashMap<String, Object>();
		for(int i=0;i<modbusDataLen/2;i++){
			List<Object> val = LcComonUtils.decodeValue(i, modbusDataBuf, dataTabList);
			Object o = val.get(1);
			if(o != null)
				data.put(i + "", val);
		}
		
		datePack.setData(data);
		return datePack;
	}

}
