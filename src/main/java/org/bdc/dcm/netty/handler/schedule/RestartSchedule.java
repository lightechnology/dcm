package org.bdc.dcm.netty.handler.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class RestartSchedule implements Runnable{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ChannelHandlerContext ctx;
	
	private boolean state;
	
	public RestartSchedule(ChannelHandlerContext ctx) {
		super();
		this.ctx = ctx;
	}

	@Override
	public void run() {
		if(!state){
			ctx.connect(ctx.channel().remoteAddress()).addListener(new GenericFutureListener<Future<Void>>() {
				
				@Override
				public void operationComplete(Future<Void> future) throws Exception {
					state = future.isSuccess();
					logger.info("断线重连：{}",state);
				}
			});
		}
	}

}
