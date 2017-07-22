package org.bdc.dcm.netty.handler;

import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;

public class LcmdbHandler extends DataHandler {

	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public LcmdbHandler(NettyBoot nettyBoot) {
		super(nettyBoot);
	}

	@Override
	public void read(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.read(ctx);
		ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
		ByteBuf in = alloc.buffer();
		in.writeBytes(
				Public.hexString2bytes("FE A5 01 12 16 00 12 4B 00 0A DC 89 5B 00 01 03 00 80 00 0F 04 26 0D"));
		ctx.writeAndFlush(in);
	}

	

}
