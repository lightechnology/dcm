package org.bdc.dcm.netty.channel.tcp;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.tcp.TcpLcmdbDecoder;
import org.bdc.dcm.netty.coder.tcp.TcpLcmdbEncoder;
import org.bdc.dcm.netty.framer.LcmdbFrameDecoder;
import org.bdc.dcm.netty.handler.DataHandler;
import org.bdc.dcm.netty.handler.LcmdbHandler;
import org.bdc.dcm.vo.e.DataType;

public class TcpLcmdbChannelInitializer extends AbstractChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		super.initChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("framer", new LcmdbFrameDecoder());
		//pipeline.addLast("decoder", new TcpLcmdbDecoder(getNettyBoot(),DataType.Lcmdb));
		//pipeline.addLast("encoder", new TcpLcmdbEncoder(getNettyBoot(),DataType.Lcmdb));
        //pipeline.addLast("dataHandler", new DataHandler(getNettyBoot()));
		pipeline.addLast("dataHandler", new LcmdbHandler(getNettyBoot()));
	}

}
