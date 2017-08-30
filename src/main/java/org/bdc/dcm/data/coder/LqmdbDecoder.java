package org.bdc.dcm.data.coder;


import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.convert.lqmdb.LqmdbTypeConvert;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import static org.bdc.dcm.data.coder.utils.CommUtils.*;

public class LqmdbDecoder implements  DataDecoder<ByteBuf>  {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final DataTabConf dataTabConf;
	
	private int macLen = 6;
	
	public LqmdbDecoder() {
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	/**
	 * 能进来的都是可读的不用判断
	 * 数据id: 功能码+寄存器编号
	 * 数据值  : 寄存器值
	 */
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, ByteBuf in){
		
		byte[] mac = new byte[macLen];
		in.readBytes(mac);
		
		return modbusParse(dataTabConf.getDataTabConf(DataType.Lqmdb.name()), in, mac, LqmdbTypeConvert.getConvert());
			
			
	}
}
