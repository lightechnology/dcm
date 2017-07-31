package org.bdc.dcm.netty.channel.tcp;

import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.tcp.TcpLqmdbDecoder;
import org.bdc.dcm.netty.coder.tcp.TcpLqmdbEncoder;
import org.bdc.dcm.netty.framer.LqmdbFrameDecoder;
import org.bdc.dcm.netty.handler.LqmdbDataHandler;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class TcpLqmdbChannelInitializer extends AbstractChannelInitializer<SocketChannel>  {
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		super.initChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast( new LqmdbFrameDecoder());
		pipeline.addLast("encoder", new TcpLqmdbDecoder(getNettyBoot()));
		pipeline.addLast("decoder", new TcpLqmdbEncoder(getNettyBoot()));
		pipeline.addLast("lqDataHandler", new LqmdbDataHandler(getNettyBoot()));
	}
}
