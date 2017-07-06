package org.bdc.dcm.data.coder.intf;

import io.netty.channel.ChannelHandlerContext;

import org.bdc.dcm.vo.DataPack;

public interface DataDecoder<I> {
	public DataPack data2Package(ChannelHandlerContext ctx, I msg);
}
