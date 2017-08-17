package org.bdc.dcm.netty;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
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
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Handler implementation for the echo server.
 */
public class EchoServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static ByteBuf firstMessage;
    private static Logger logger = LoggerFactory.getLogger(EchoServerHandler.class);
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static int  maxReceiveClient = 0;
    private static long maxRunTime = 0L;
    private static AtomicInteger onLine = new AtomicInteger(0);
    
    static{
    	try {
			Properties properties = new Properties();
			InputStream in = EchoServerHandler.class.getClassLoader().getResourceAsStream("org/bdc/dcm/netty/properties/EchoServerHandler.properties");
			properties.load(in);
			maxReceiveClient = Integer.valueOf((String) properties.get("maxReceiveClient"));
			in.close();
			logger.info("EchoServerHandler:{}",properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * Creates a client-side handler.
     */
    public EchoServerHandler() {
        firstMessage = Unpooled.buffer(EchoClient.SIZE);
        for (int i = 0; i < firstMessage.capacity(); i ++) {
            firstMessage.writeByte((byte) 0x11);
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
		
		if(channelGroup.size() < maxReceiveClient)
			logger.error("删除接收网关,cg个数："+channelGroup.size());
		else
			logger.error("有效在线用户："+onLine.decrementAndGet());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		int curNum = channelGroup.size();
		
		if(curNum < maxReceiveClient){
			Channel channel = ctx.channel();
			channelGroup.add(channel);
			logger.error("channelId:"+channel.id().asLongText());
		}else{
			ctx.writeAndFlush(outInitBuf());
			logger.error("有效在线用户："+onLine.incrementAndGet());
		}
		
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		
		boolean isReceiveClient = false;
		long startTime = System.currentTimeMillis();
		Iterator<Channel> iterator = channelGroup.iterator();
		
		long runTime = System.currentTimeMillis() - startTime;
		while(iterator.hasNext()){
			Channel channel = iterator.next();
			channel.writeAndFlush(outMessageBuf(msg));
			if(channel.id().asLongText().equals(ctx.channel().id().asLongText())){
				isReceiveClient = true;
			}
		}

		if(runTime > maxRunTime){
			maxRunTime = runTime;
			logger.error("最大运行时间:{},接收信息客户端个数：{}",maxRunTime,channelGroup.size());
		}
		
		
		if(!isReceiveClient)
			ctx.writeAndFlush(outMessageBuf(msg));
		
		msg.clear();
		
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

}