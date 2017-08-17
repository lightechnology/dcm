package org.bdc.dcm.netty;

import java.util.Arrays;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class TestFrameDecoder extends ByteToMessageDecoder{

	private static final int packLen = 60;
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		while(in.isReadable() && in.readableBytes() >= packLen ){
			byte[] dst = new byte[packLen];
			in.readBytes(dst);
			ByteBuf buf = in.alloc().buffer(dst.length);
			buf.writeBytes(dst);
			out.add(buf);
			
			dst = null;
		}
		
	}


}
