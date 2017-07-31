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
			new Thread(loopThread).start();
		}else{//第二笔数据来
			//TODO --------测试代码
		}
	}
	
	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		super.close(ctx, promise);
		loopThread.setRun(false);
	}
	
}
