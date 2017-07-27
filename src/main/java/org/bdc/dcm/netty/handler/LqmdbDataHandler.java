package org.bdc.dcm.netty.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.lqmdb.InitPack;
import org.bdc.dcm.netty.lqmdb.LqFun;
import org.bdc.dcm.netty.lqmdb.LqRepository;
import org.bdc.dcm.vo.DataPack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class LqmdbDataHandler extends DataHandler {

	private static Set<ChannelHandlerContext> channelHandlerContexts = Collections.synchronizedSet(new HashSet<>());
	
	private static boolean writing = false;
	
	static{
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
					try {
						List<ChannelHandlerContext> list = new ArrayList<>(channelHandlerContexts);
						for(int i=0;i<list.size();i++){
							ChannelHandlerContext ctx = list.get(i);
							if(ctx.channel().isRegistered() && !writing)
								handlerInit(ctx);
							else{
								if(logger.isDebugEnabled())
									logger.debug("channel不存在:{}",ctx.channel().id());
								channelHandlerContexts.remove(ctx);
							}
						}
						if(logger.isDebugEnabled())
							logger.debug("定时轮训设备状态,共：{} 个",list.size());
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			
		}, 1000,3000);// 两分钟后启动任务
	}
	
	public LqmdbDataHandler(NettyBoot nettyBoot) {
		super(nettyBoot); 
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		channelHandlerContexts.add(ctx);
		handlerInit(ctx);
	}
	

	

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		channelHandlerContexts.remove(ctx);
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, DataPack msg)
			throws Exception {
		super.messageReceived(ctx, msg);
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		super.write(ctx, msg, promise);
		writing = true;
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		super.flush(ctx);
		writing = false;
	}

	/**
	 * 发送初始化命令读取状态
	 * @param ctx
	 * @throws Exception
	 */
	private static void handlerInit(ChannelHandlerContext ctx) throws Exception{
		LqRepository repository = LqRepository.build();
		List<InitPack> lqInitPacks = repository.findAll();
		if(logger.isDebugEnabled())
			logger.debug("lq初始化数据： {}",lqInitPacks);
		for(InitPack pack:lqInitPacks){
			byte[] addrs = pack.getDeviceAddrs();
			for(byte addr:addrs)
				LqFun.sendInitPack(ctx,addr,pack.getMac());
		}
	}
}
