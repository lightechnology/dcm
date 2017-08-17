package org.bdc.dcm.netty.channel.tcp;

import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.tcp.TcpYgdqmdbDecoder;
import org.bdc.dcm.netty.coder.tcp.TcpYgdqmdbEncoder;
import org.bdc.dcm.netty.framer.YgdqmdbFrameDecoder;
import org.bdc.dcm.netty.handler.YgdqmdbDataHandler;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class TcpYgdqmdbChannelInitializer extends AbstractChannelInitializer<SocketChannel>   {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		super.initChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast( new YgdqmdbFrameDecoder());
		pipeline.addLast("encoder", new TcpYgdqmdbDecoder(getNettyBoot()));
		pipeline.addLast("decoder", new TcpYgdqmdbEncoder(getNettyBoot()));
		pipeline.addLast("lqDataHandler", new YgdqmdbDataHandler(getNettyBoot()));
	}
}
