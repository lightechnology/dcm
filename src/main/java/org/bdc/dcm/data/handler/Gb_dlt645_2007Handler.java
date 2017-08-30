package org.bdc.dcm.data.handler;

import org.bdc.dcm.data.convert.gb_dlt645_2007.GB_DLT645_2007CheckStateThread;
import org.bdc.dcm.data.convert.gb_dlt645_2007.GB_DLT645_2007LoopInfo;
import org.bdc.dcm.netty.ChannelHandlerContextDecorator;
import org.bdc.dcm.netty.handler.DataHandler;
import org.bdc.dcm.netty.handler.DataHandlerDecorator;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.LoopInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gb_dlt645_2007Handler extends DataHandlerDecorator{

	private Logger logger = LoggerFactory.getLogger(Gb_dlt645_2007Handler.class);
	
	private static GB_DLT645_2007CheckStateThread loopThread = new GB_DLT645_2007CheckStateThread();

	@Override
	public void messageReceived(ChannelHandlerContextDecorator ctx, DataPack msg) {
		if(!loopThread.isRun()){
			DataHandler.CACHED_THREAD_POOL.execute(loopThread);
			//第一个包为 路由上传 mac包 第二条开始 为混合包
			loopThread.addLoopInfo(new GB_DLT645_2007LoopInfo(ctx, msg.getMac()));
		}
	}

	@Override
	public void channelActive(ChannelHandlerContextDecorator ctx) {}

	@Override
	public void channelInactive(ChannelHandlerContextDecorator ctx) {
		loopThread.removeLoopInfo(ctx);	
	}

}
