package org.bdc.dcm.netty.coder.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.log.ByteBuf4Log;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.coder.Decoder;
import org.slf4j.Logger;

public abstract class TcpDecoder extends Decoder<ByteBuf> {

	public TcpDecoder(Logger logger, NettyBoot nettyBoot,
			DataDecoder<ByteBuf> decoder) {
		super(new ByteBuf4Log(TAG, logger), nettyBoot, decoder);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
        doDecode(ctx, msg, out);
	}

}
