package org.bdc.dcm.netty;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultChannelProgressivePromise;

public class HyChannelHandlerContext{

	private ChannelHandlerContext ctx;	
	
	public HyChannelHandlerContext(ChannelHandlerContext ctx) {
		this.ctx = ctx;
		
	}

	public ByteBufAllocator alloc(){
		return ctx.alloc();
	}
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public ChannelFuture writeAndFlush(Object msg){
		Channel channel = ctx.channel();
		if(channel.isWritable())
			return channel.writeAndFlush(msg);
		else
			return null;
	}
	
	public void
}
