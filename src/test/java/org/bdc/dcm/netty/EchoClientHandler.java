package org.bdc.dcm.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final ByteBuf firstMessage;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Creates a client-side handler.
     */
    public EchoClientHandler() {
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
		logger.error("id:{},data:{}",ctx.channel().id().asLongText(),Public.byte2hex(bs));
		ctx.writeAndFlush(buf());
	}
}