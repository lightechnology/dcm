package org.bdc.dcm.data.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.vo.DataPack;

public class LcmdbEncoder implements DataEncoder<ByteBuf> {
	
	private final DataTabConf dataTabConf;

	public LcmdbEncoder() {
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	
	// 通过DataTypeConf接口获取编码规则
	@Override
	public ByteBuf package2Data(ChannelHandlerContext ctx, DataPack msg) {
	    // 待编写编码部分的代码
		return null;
	}
	
}
