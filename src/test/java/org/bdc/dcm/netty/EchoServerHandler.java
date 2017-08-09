package org.bdc.dcm.netty;


import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Handler implementation for the echo server.
 */
public class EchoServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static ByteBuf firstMessage;
    private static Logger logger = LoggerFactory.getLogger(EchoServerHandler.class);
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final int  maxReceiveNum = 1;
    private static long maxRunTime = 0L;
    private static AtomicInteger onLine = new AtomicInteger(0);
    /**
     * Creates a client-side handler.
     */
    public EchoServerHandler() {
        firstMessage = Unpooled.buffer(EchoClient.SIZE);
        for (int i = 0; i < firstMessage.capacity(); i ++) {
            firstMessage.writeByte((byte) i);
        }
    }   

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		
		if(channelGroup.size() < maxReceiveNum)
			System.err.println("删除接收网关,cg个数："+channelGroup.size());
		else
			System.err.println("有效在线用户："+onLine.decrementAndGet());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		int curNum = channelGroup.size();
		
		if(curNum < maxReceiveNum){
			Channel channel = ctx.channel();
			channelGroup.add(channel);
			System.err.println("channelId:"+channel.id().asLongText());
		}else{
			ctx.writeAndFlush(outInitBuf());
			System.err.println("有效在线用户："+onLine.incrementAndGet());
		}
		
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		
		boolean isReceiveClient = false;
		long startTime = System.currentTimeMillis();
		Iterator<Channel> iterator = channelGroup.iterator();
		while(iterator.hasNext()){
			Channel channel = iterator.next();
			channel.writeAndFlush(outMessageBuf(msg));
			if(channel.id().asLongText().equals(ctx.channel().id().asLongText())){
				isReceiveClient = true;
			}
		}
		if(!isReceiveClient)
			ctx.writeAndFlush(outMessageBuf(msg)).addListener(new GenericFutureListener<Future<Void>>() {

				@Override
				public void operationComplete(Future<Void> future) throws Exception {
					logger.error("operationComplete");
				}
			});
		ctx.writeAndFlush(outMessageBuf(msg));
		int readableBytes = msg.readableBytes();
		clearAll(msg);
		long runTime = System.currentTimeMillis() - startTime;
		if(runTime > maxRunTime){
			maxRunTime = runTime;
			logger.error("最大运行时间:{},需转发包大小：{},接收信息客户端个数：{}",maxRunTime,readableBytes,channelGroup.size());
		}
	}
	
	private ByteBuf outInitBuf() {
		byte[] bs = new byte[firstMessage.readableBytes()];
		firstMessage.getBytes(0, bs);
		ByteBuf buf = firstMessage.alloc().buffer(bs.length);
		buf.writeBytes(bs);
		return buf;
	}
	private ByteBuf outMessageBuf(ByteBuf msg) {
		byte[] bs = new byte[msg.readableBytes()];
		msg.getBytes(0,bs);
		ByteBuf buf = msg.alloc().buffer(bs.length);
		buf.writeBytes(bs);
		return buf;
	}
	private void clearAll(ByteBuf msg){
		byte[] bs = new byte[msg.readableBytes()];
		msg.readBytes(bs);
	}
}