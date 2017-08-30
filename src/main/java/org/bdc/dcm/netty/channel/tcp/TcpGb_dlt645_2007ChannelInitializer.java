package org.bdc.dcm.netty.channel.tcp;

import org.bdc.dcm.data.handler.Gb_dlt645_2007Handler;
import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.tcp.TcpGb_dlt645_2007Decoder;
import org.bdc.dcm.netty.coder.tcp.TcpGb_dlt645_2007Encoder;
import org.bdc.dcm.netty.framer.Gb_dlt645_2007FrameDecoder;
import org.bdc.dcm.netty.handler.DataHandler;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TcpGb_dlt645_2007ChannelInitializer extends AbstractChannelInitializer<SocketChannel>   {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		super.initChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new LoggingHandler(LogLevel.INFO));
		pipeline.addLast( new Gb_dlt645_2007FrameDecoder());
		pipeline.addLast("encoder", new TcpGb_dlt645_2007Decoder(getNettyBoot()));
		pipeline.addLast("decoder", new TcpGb_dlt645_2007Encoder(getNettyBoot()));
		pipeline.addLast("lqDataHandler", new DataHandler(getNettyBoot(),new Gb_dlt645_2007Handler()));
	}
}
