package org.bdc.dcm.netty.handler.intf;

import org.bdc.dcm.netty.piple.HyChannelPiple;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;

public interface HyChannelHandlerContextIntf {

	/**
	 * handler 通道
	 * @return
	 */
	public HyChannelPiple piple();
	
	/**
	 * 申请内存
	 * @return
	 */
	public ByteBufAllocator alloc();
	/**
	 * 刷新
	 */
	public void flush();
	
	/**
	 * 写 做写上下水位限制
	 * @param msg
	 * @return
	 */
	public ChannelFuture write(Object msg);
	
	/**
	 * 写和刷新 做写上下水位限制
	 * @param msg
	 * @return
	 */
	public ChannelFuture writeAndFlush(Object msg);
	/**
	 * 统一通道id
	 * @return
	 */
	public String id();
}
