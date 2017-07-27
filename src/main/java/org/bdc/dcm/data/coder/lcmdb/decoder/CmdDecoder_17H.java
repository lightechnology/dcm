package org.bdc.dcm.data.coder.lcmdb.decoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.TypeConvert;
import org.bdc.dcm.data.coder.lcmdb.CommandTypeCtr;
import org.bdc.dcm.data.coder.lcmdb.vo.CommLcParam;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.netty.lcmdb.LcmdbTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import static org.bdc.dcm.data.coder.utils.CommUtils.*;
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
		
		List<DataTab> dataTabList = dataTabConf.getDataTabConf(DataType.Lcmdb.name());
		
		ByteBuf in = param.getPack();
		
		byte[] mac = new byte[macLen];
		in.readBytes(mac);
		
		//拿掉 序号 特殊点
		in.readByte();
		//配置区域-------------------------------------------------------------------
		int baseRegAddr = 128;
		TypeConvert convert = LcmdbTypeConvert.getConvert();
		//------------------modebus 数据+crc------------------------------------------------------
		byte addr = in.readByte();//地址
		byte funcode = in.readByte();
		if(funcode == (byte)0x03) {
			return funCode_03(dataTabList, in, mac, baseRegAddr, convert, addr);
		}else if(funcode == (byte)0x05) {//写单个寄存器
			int i = in.readInt();
			Map<String, Object> data = new HashMap<>();
			data.put(LcmdbTypeConvert.DATATYPE_JDQSTATE+"", makeMapValue("", i>0));
			DataPack dataPack = getInitInfoDataPack(Public.byte2hex(mac) + " " +Public.byte2hex_ex(addr));
			dataPack.setData(data);
			in.readShort();//读完
			return dataPack;
		}else {
			return funCode_UnKnow(in, mac, addr);
		}
	}

	
}
