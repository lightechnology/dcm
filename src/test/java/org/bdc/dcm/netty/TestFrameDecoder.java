package org.bdc.dcm.netty;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class TestFrameDecoder extends ByteToMessageDecoder{

	private static int packLen;
	
	private static Logger logger = LoggerFactory.getLogger(TestFrameDecoder.class);
	
	static{
		try {
			Properties properties = new Properties();
			InputStream in = EchoServerHandler.class.getClassLoader().getResourceAsStream("org/bdc/dcm/netty/properties/TestFrameDecoder.properties");
			properties.load(in);
			packLen = Integer.valueOf((String) properties.get("packLen"));
			in.close();
			logger.info("TestFrameDecoder:{}",properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
