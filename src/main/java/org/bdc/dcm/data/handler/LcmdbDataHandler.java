package org.bdc.dcm.data.handler;

import org.bdc.dcm.data.convert.lcmdb.LcmdbLoopCheckStateThread;
import org.bdc.dcm.data.convert.lcmdb.LcmdbLoopInfo;
import org.bdc.dcm.netty.ChannelHandlerContextDecorator;
import org.bdc.dcm.netty.handler.DataHandler;
import org.bdc.dcm.netty.handler.DataHandlerDecorator;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LcmdbDataHandler extends DataHandlerDecorator {

	private Logger logger = LoggerFactory.getLogger(LcmdbDataHandler.class);
	
	private static LcmdbLoopCheckStateThread loopThread = new LcmdbLoopCheckStateThread();

//	@Override
//	protected void messageReceived(ChannelHandlerContext ctx, DataPack msg) throws Exception {
//		super.messageReceived(ctx, msg);
//		if(!loopThread.isRun())
//			CACHED_THREAD_POOL.execute(loopThread);
//		
//		loopThread.addLoopInfo(new LcmdbLoopInfo(ctx, msg.getMac()));
//	}
//
//	@Override
//	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		loopThread.removeLoopInfo(ctx);
//		super.channelInactive(ctx);
//		
//	}

	@Override
	public void messageReceived(ChannelHandlerContextDecorator ctx, DataPack msg) {
		if(!loopThread.isRun())
			DataHandler.CACHED_THREAD_POOL.execute(loopThread);
		
		loopThread.addLoopInfo(new LcmdbLoopInfo(ctx, msg.getMac()));		
	}

	@Override
	public void channelActive(ChannelHandlerContextDecorator ctx) {
			
	}

	@Override
	public void channelInactive(ChannelHandlerContextDecorator ctx) {
		loopThread.removeLoopInfo(ctx);	
	}

}
