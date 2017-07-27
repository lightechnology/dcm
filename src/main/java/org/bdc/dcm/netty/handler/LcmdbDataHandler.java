package org.bdc.dcm.netty.handler;

import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.lcmdb.LcmdbLoopCheckStateThread;
import org.bdc.dcm.vo.DataPack;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class LcmdbDataHandler  extends DataHandler {

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
			new Thread(loopThread).start();
		}else{//第二笔数据来
			//TODO --------测试代码
			//LcFunTest.go(ctx, msg);
		}
	}
	
	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		super.close(ctx, promise);
		loopThread.setRun(false);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

}