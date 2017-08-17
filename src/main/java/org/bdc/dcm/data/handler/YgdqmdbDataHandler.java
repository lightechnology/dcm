package org.bdc.dcm.data.handler;

import org.bdc.dcm.data.convert.ygdqmdb.YgdqmdbCheckStateThread;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.handler.DataHandler;
import org.bdc.dcm.vo.DataPack;

import io.netty.channel.ChannelHandlerContext;

public class YgdqmdbDataHandler  extends DataHandler {

	private YgdqmdbCheckStateThread loopThread = new YgdqmdbCheckStateThread();
	
	public YgdqmdbDataHandler(NettyBoot nettyBoot) {
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
