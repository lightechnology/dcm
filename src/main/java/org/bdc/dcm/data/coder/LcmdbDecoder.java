package org.bdc.dcm.data.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.vo.DataPack;

public class LcmdbDecoder implements DataDecoder<ByteBuf> {
	
	private final DataTabConf dataTabConf;

	public LcmdbDecoder() {
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	
	// 通过DataTypeConf接口获取解码规则
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, ByteBuf msg) {
	    // 待编写解码部分的代码
		return null;
	}
	
}
