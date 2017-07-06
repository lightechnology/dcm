package org.bdc.dcm.data.coder;

import io.netty.channel.ChannelHandlerContext;

import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.vo.DataPack;

public class JmstrEncoder implements DataEncoder<String> {
	
	// 通过DataTypeConf接口获取编码规则
	@Override
	public String package2Data(ChannelHandlerContext ctx, DataPack msg) {
		return null;
	}
	
}
