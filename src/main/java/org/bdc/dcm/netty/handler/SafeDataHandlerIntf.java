package org.bdc.dcm.netty.handler;

import org.bdc.dcm.vo.DataPack;

import io.netty.channel.ChannelHandlerContext;

public interface SafeDataHandlerIntf {

	/**
	 * 通道状态发生变化 
	 * @param ctx
	 */
	public void channelActive(ChannelHandlerContext ctx);

	public void messageReceived(ChannelHandlerContext ctx, DataPack msg);

	public void channelInactive(ChannelHandlerContext ctx);
}
