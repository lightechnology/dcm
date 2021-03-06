package org.bdc.dcm.netty.channel.http;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import org.bdc.dcm.data.coder.http.factory.HttpResponseFactory;
import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.http.HttpLcmdJsnDecoder;
import org.bdc.dcm.netty.coder.http.HttpLcmdJsnEncoder;
import org.bdc.dcm.netty.handler.DataHandler;

public class HttpServerLcmdbJsnChannelInitializer extends AbstractChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		super.initChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("httpAggregator", new HttpObjectAggregator(65536));
		pipeline.addLast("clientDecoder", new HttpLcmdJsnDecoder(getNettyBoot()));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("clientEncoder", new HttpLcmdJsnEncoder(getNettyBoot(), new HttpResponseFactory()));
		pipeline.addLast("httpChunked", new ChunkedWriteHandler());
		pipeline.addLast("dataHandler", new DataHandler(getNettyBoot()));
	}

}
