package org.bdc.dcm.netty.coder.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ExceptionCaught extends ChannelHandlerAdapter {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (logger.isErrorEnabled()) {
		    logger.error(cause.getMessage(), cause);
		}
		super.exceptionCaught(ctx, cause);
	}

}
