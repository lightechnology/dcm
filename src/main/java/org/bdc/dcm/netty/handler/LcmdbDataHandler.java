package org.bdc.dcm.netty.handler;

import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.lcmdb.LcmdbLoopCheckStateThread;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public class LcmdbDataHandler  extends DataHandler {

	private Logger logger = LoggerFactory.getLogger(LcmdbDataHandler.class);
	
	private LcmdbLoopCheckStateThread loopThread = new LcmdbLoopCheckStateThread();
 
	public LcmdbDataHandler(NettyBoot nettyBoot) {
		super(nettyBoot);
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, DataPack msg) throws Exception {
		super.messageReceived(ctx, msg);
		if(!loopThread.isRun()){
			loopThread.setCtx(ctx);
			loopThread.setMac(msg.getMac());
			CACHED_THREAD_POOL.execute(loopThread);
		}else{//第二笔数据来
			
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		loopThread.setRun(false);
		loopThread = null;
		super.channelInactive(ctx);
		
	}

}
