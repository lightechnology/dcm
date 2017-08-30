package org.bdc.dcm.netty;

import java.net.SocketAddress;

import org.bdc.dcm.netty.handler.intf.HyChannelHandlerContextIntf;
import org.bdc.dcm.netty.piple.HyChannelPiple;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

/**
 * 常用功能封装包装
 * @author 李哲弘
 *
 */
public class ChannelHandlerContextDecorator implements HyChannelHandlerContextIntf{

	private ChannelHandlerContext ctx;	
	
	private HyChannelPiple piple;
	
	public SocketAddress localAddress(){
		return ctx.channel().localAddress();
	}
	public SocketAddress remoteAddress(){
		return ctx.channel().remoteAddress();
	}
	public String id(){
		return ctx.channel().id().asLongText();
	}
	
	public ChannelHandlerContextDecorator(ChannelHandlerContext ctx) {
		this.ctx = ctx;
		this.piple = new HyChannelPiple(ctx.pipeline());
	}

	public HyChannelPiple piple(){
		return piple;
	}
	
	public ByteBufAllocator alloc(){
		return ctx.alloc();
	}
	
	public void flush(){
		ctx.channel().flush();
	}
	
	public ChannelFuture write(Object msg){
		Channel channel = ctx.channel();
		if(channel.isWritable())
			return channel.write(msg);
		else 
			return null;
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

	public boolean isRemoved() {
		return ctx.isRemoved();
	}
	
}