package org.bdc.dcm.netty.coder.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;

import java.util.List;

import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.data.log.HttpContent4Log;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.coder.Encoder;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;

public abstract class HttpEncoder extends Encoder<DataPack, HttpContent> {
	
	public HttpEncoder(Logger logger, NettyBoot nettyBoot, DataEncoder<HttpContent> encoder) {
		super(new HttpContent4Log(TAG, logger), nettyBoot, encoder);
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, DataPack msg, List<Object> out) {
		doEncode(ctx, msg, out);
	}
	
}