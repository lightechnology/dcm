package org.bdc.dcm.data.coder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.coder.intf.TypeConvert;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.netty.lcmdb.LcmdbTypeConvert;
import org.bdc.dcm.netty.lqmdb.LqmdbTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

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
		
		List<DataTab> dataTabList = dataTabConf.getDataTabConf(DataType.Lqmdb.name());
		
		
		byte[] mac = new byte[macLen];
		in.readBytes(mac);
		
		int baseRegAddr = 0;
		TypeConvert convert = LqmdbTypeConvert.getConvert();

		//------------------modebus 数据+crc------------------------------------------------------
		byte addr = in.readByte();//地址
		byte funcode = in.readByte();
		if(funcode == (byte)0x03) {
			return funCode_03(dataTabList, in, mac, baseRegAddr, convert, addr);
		}else {
			return funCode_UnKnow(in, mac, addr);
		}
			
			
	}
}
