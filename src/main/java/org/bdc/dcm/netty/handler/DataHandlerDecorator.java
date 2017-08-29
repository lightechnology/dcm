package org.bdc.dcm.netty.handler;

import org.bdc.dcm.netty.ChannelHandlerContextDecorator;
import org.bdc.dcm.vo.DataPack;

import io.netty.channel.ChannelHandlerContext;

/**
 * 包装类,降低控制器使用难度 统一学标准结合上下水位
 * @author 李哲弘
 *
 */
public abstract class DataHandlerDecorator implements SafeDataHandlerIntf{

	@Override
	public void channelInactive(ChannelHandlerContext ctx){
		channelInactive(new ChannelHandlerContextDecorator(ctx));
	}
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		channelActive(new ChannelHandlerContextDecorator(ctx));
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, DataPack msg) {
		messageReceived(new ChannelHandlerContextDecorator(ctx), msg);
	}
	/**
	 * 接收到数据
	 * @param ctx
	 * @param msg
	 */
	public abstract void messageReceived(ChannelHandlerContextDecorator ctx, DataPack msg);
	
	public abstract void channelActive(ChannelHandlerContextDecorator ctx);
	
	public abstract void channelInactive(ChannelHandlerContextDecorator ctx);
}
