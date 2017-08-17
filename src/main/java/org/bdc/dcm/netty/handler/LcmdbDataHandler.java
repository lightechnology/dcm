package org.bdc.dcm.netty.handler;

import org.bdc.dcm.data.convert.lcmdb.LcmdbLoopCheckStateThread;
import org.bdc.dcm.data.convert.lcmdb.LcmdbLoopInfo;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public class LcmdbDataHandler extends DataHandler {

	private Logger logger = LoggerFactory.getLogger(LcmdbDataHandler.class);
	
	private static LcmdbLoopCheckStateThread loopThread = new LcmdbLoopCheckStateThread();
 
	public LcmdbDataHandler(NettyBoot nettyBoot) {
		super(nettyBoot);
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, DataPack msg) throws Exception {
		super.messageReceived(ctx, msg);
		if(!loopThread.isRun())
			CACHED_THREAD_POOL.execute(loopThread);
		
		loopThread.addLoopInfo(new LcmdbLoopInfo(ctx, msg.getMac()));
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		loopThread.removeLoopInfo(ctx);
		super.channelInactive(ctx);
		
	}

}
