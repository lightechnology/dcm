package org.bdc.dcm.netty.channel.http;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

import org.bdc.dcm.data.coder.http.factory.HttpRequestFactory;
import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.http.HttpJmjsnDecoder;
import org.bdc.dcm.netty.coder.http.HttpJmjsnEncoder;
import org.bdc.dcm.netty.handler.DataHandler;

public class HttpClientJmjsnChannelInitializer extends AbstractChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		super.initChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("decoder", new HttpResponseDecoder());
		pipeline.addLast("clientDecoder", new HttpJmjsnDecoder(getNettyBoot()));
		pipeline.addLast("encoder", new HttpRequestEncoder());
		pipeline.addLast("clientEncoder", new HttpJmjsnEncoder(getNettyBoot(), new HttpRequestFactory()));
		pipeline.addLast("dataHandler", new DataHandler(getNettyBoot()));
	}

}
