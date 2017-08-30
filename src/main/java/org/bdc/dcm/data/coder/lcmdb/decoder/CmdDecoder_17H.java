package org.bdc.dcm.data.coder.lcmdb.decoder;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.lcmdb.CommandTypeCtr;
import org.bdc.dcm.data.coder.lcmdb.vo.CommLcParam;
import org.bdc.dcm.data.convert.lcmdb.LcmdbTypeConvert;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
	
		ByteBuf in = param.getPack();
		
		byte[] mac = new byte[macLen];
		in.readBytes(mac);
		
		//拿掉 序号 特殊点
		in.readByte();
		return modbusParse(dataTabConf.getDataTabConf(DataType.Lcmdb.name()), in, mac, LcmdbTypeConvert.getConvert());
	}

	
}
