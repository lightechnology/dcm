package org.bdc.dcm.netty.coder.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;

import java.util.List;

import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.log.HttpContent4Log;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.coder.Decoder;
import org.slf4j.Logger;

public abstract class HttpDecoder extends Decoder<HttpContent> {
	
	public HttpDecoder(Logger logger, NettyBoot nettyBoot, DataDecoder<HttpContent> decoder) {
		super(new HttpContent4Log(TAG, logger), nettyBoot, decoder);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, HttpContent msg,
			List<Object> out) throws Exception {
		doDecode(ctx, msg, out);
	}

}
