package org.bdc.dcm.netty.handler.intfImpl;

import org.bdc.dcm.netty.handler.intf.WriteTaskIntf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class WriteTask implements WriteTaskIntf {

	private ChannelHandlerContext ctx; 
	private Object msg; 
	private ChannelPromise promise;
	public WriteTask(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		super();
		this.ctx = ctx;
		this.msg = msg;
		this.promise = promise;
	}

	@Override
	public void invoke() throws Exception {
		if(!ctx.isRemoved())
			ctx.writeAndFlush(msg, promise);
	}
}
