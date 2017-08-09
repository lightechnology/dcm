package org.bdc.dcm.data.coder;

import static org.bdc.dcm.data.coder.utils.CommUtils.modbusParse;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.netty.ygdqmdb.YgdqmdbTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class YgdqmdbDecoder implements  DataDecoder<ByteBuf>   {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final DataTabConf dataTabConf;
	
	private int macLen = 6;
	
	public YgdqmdbDecoder() {
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, ByteBuf in) {
		byte[] mac = new byte[macLen];
		in.readBytes(mac);
		
		return modbusParse(dataTabConf.getDataTabConf(DataType.Ygdqmdb.name()), in, mac, YgdqmdbTypeConvert.getConvert());
			
	}

}
