package org.bdc.dcm.data.coder.intf;

import io.netty.channel.ChannelHandlerContext;

import org.bdc.dcm.vo.DataPack;

public interface DataEncoder<I> {
	public I package2Data(ChannelHandlerContext ctx, DataPack msg);
}
