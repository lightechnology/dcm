package org.bdc.dcm.netty.channel.tcp;

import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.tcp.TcpKlvDecoder;
import org.bdc.dcm.netty.coder.tcp.TcpKlvEncoder;
import org.bdc.dcm.netty.framer.KlvFrameDecoder;
import org.bdc.dcm.netty.handler.DataHandler;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class TcpKlvChannelInitializer extends AbstractChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		super.initChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("framer", new KlvFrameDecoder());
		pipeline.addLast("decoder", new TcpKlvDecoder(getNettyBoot()));
		pipeline.addLast("encoder", new TcpKlvEncoder(getNettyBoot()));
		pipeline.addLast("dataHandler", new DataHandler(getNettyBoot()));
	}

}
