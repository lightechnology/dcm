package org.bdc.dcm.netty.channel.tcp;

import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.tcp.TcpLqDecoder;
import org.bdc.dcm.netty.coder.tcp.TcpLqEncoder;
import org.bdc.dcm.netty.framer.LqFrameDecoder;
import org.bdc.dcm.netty.handler.LqDataHandler;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class TcpLqChannelInitializer extends AbstractChannelInitializer<SocketChannel>  {
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		super.initChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast( new LqFrameDecoder());
		pipeline.addLast("encoder", new TcpLqDecoder(getNettyBoot()));
		pipeline.addLast("decoder", new TcpLqEncoder(getNettyBoot()));
		pipeline.addLast("lqDataHandler", new LqDataHandler(getNettyBoot()));
	}
}
