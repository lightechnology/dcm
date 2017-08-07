package org.bdc.dcm.netty;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final ByteBuf firstMessage;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static HyChannelGroupIntf<String> channelGroup = new HyChannelGroup<>(GlobalEventExecutor.INSTANCE);
    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();
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
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		Channel channel = channelGroup.findByIndetity("");
		if(channel == null){
			Channel myChannel = ctx.channel();
			channelGroup.add(myChannel, "");
			System.err.println("channelId:"+myChannel.id().asLongText());
		}else{
			ctx.writeAndFlush(buf());
		}
	}

	private ByteBuf buf() {
		byte[] bs = new byte[firstMessage.readableBytes()];
		firstMessage.getBytes(0, bs);
		ByteBuf buf = firstMessage.alloc().buffer(bs.length);
		buf.writeBytes(bs);
		return buf;
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		byte[] bs = new byte[msg.readableBytes()];
		msg.readBytes(bs);
		Channel channel = channelGroup.findByIndetity("");
		channel.writeAndFlush(buf());
		ctx.writeAndFlush(bs);
	}
}