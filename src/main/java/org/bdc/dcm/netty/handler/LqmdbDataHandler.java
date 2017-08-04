package org.bdc.dcm.netty.handler;

import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.lqmdb.LqcmdbLoopCheckStateThread;
import org.bdc.dcm.vo.DataPack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class LqmdbDataHandler extends DataHandler {
	
	private LqcmdbLoopCheckStateThread loopThread = new LqcmdbLoopCheckStateThread();
	
	public LqmdbDataHandler(NettyBoot nettyBoot) {
		super(nettyBoot); 
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, DataPack msg)
			throws Exception {
		super.messageReceived(ctx, msg);
		if(!loopThread.isRun()){
			loopThread.setCtx(ctx);
			CACHED_THREAD_POOL.execute(loopThread);
		}else{//第二笔数据来
			//TODO --------测试代码
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		loopThread.setRun(false);
		loopThread = null;
		super.channelInactive(ctx);
	}

	
}
