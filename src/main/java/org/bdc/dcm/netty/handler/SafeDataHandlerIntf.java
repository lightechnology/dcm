package org.bdc.dcm.netty.handler;

import org.bdc.dcm.vo.DataPack;

import io.netty.channel.ChannelHandlerContext;

public interface SafeDataHandlerIntf {

	public void write(ChannelHandlerContext ctx, Object msg);

	public void channelActive(ChannelHandlerContext ctx);

	public void channelInactive(ChannelHandlerContext ctx);

	public void messageReceived(ChannelHandlerContext ctx, DataPack msg);
}
