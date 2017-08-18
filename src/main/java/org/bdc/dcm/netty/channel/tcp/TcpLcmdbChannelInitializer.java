package org.bdc.dcm.netty.channel.tcp;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.bdc.dcm.data.handler.LcmdbDataHandler;
import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.tcp.TcpLcmdbDecoder;
import org.bdc.dcm.netty.coder.tcp.TcpLcmdbEncoder;
import org.bdc.dcm.netty.framer.LcmdbFrameDecoder;
import org.bdc.dcm.netty.handler.DataHandler;

public class TcpLcmdbChannelInitializer extends AbstractChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		super.initChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("log", new LoggingHandler(LogLevel.INFO));
		pipeline.addLast("framer", new LcmdbFrameDecoder());
		pipeline.addLast("decoder", new TcpLcmdbDecoder(getNettyBoot()));
		pipeline.addLast("encoder", new TcpLcmdbEncoder(getNettyBoot()));
		pipeline.addLast("dataHandler", new DataHandler(getNettyBoot(), new LcmdbDataHandler()));
       
	}

}
