package org.bdc.dcm.netty.handler;

import org.bdc.dcm.vo.DataPack;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public abstract class SafeDataHandler implements SafeDataHandlerIntf{

	
	/**
	 * 利用上下水位实现 内存 cpu控制
	 * @param ctx
	 * @param msg
	 */
	public void write(ChannelHandlerContext ctx, Object msg) {
		Channel channel = ctx.channel();
		if(channel.isWritable())
			channel.writeAndFlush(msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		doGet(ctx, null);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		doGet(ctx, null);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, DataPack msg) {
		doGet(ctx, msg);
	}
	
	public abstract void doGet(ChannelHandlerContext ctx, DataPack msg);
}
