package org.bdc.dcm.netty.coder.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.data.log.ByteBuf4Log;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.coder.Encoder;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;

public abstract class TcpEncoder extends Encoder<DataPack, ByteBuf> {

	public TcpEncoder(Logger logger, NettyBoot nettyBoot,
			DataEncoder<ByteBuf> encoder) {
		super(new ByteBuf4Log(TAG, logger), nettyBoot, encoder);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, DataPack msg,
			List<Object> out) {
        doEncode(ctx, msg, out);
	}

}