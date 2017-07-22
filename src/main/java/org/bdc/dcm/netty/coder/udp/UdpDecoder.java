package org.bdc.dcm.netty.coder.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import java.util.List;

import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.log.DatagramPacket4Log;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.coder.Decoder;
import org.slf4j.Logger;

public abstract class UdpDecoder extends Decoder<DatagramPacket> {
	
	public UdpDecoder(Logger logger, NettyBoot nettyBoot, DataDecoder<DatagramPacket> decoder) {
		super(new DatagramPacket4Log(TAG, logger), nettyBoot, decoder);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, DatagramPacket msg,
			List<Object> out) throws Exception {
		doDecode(ctx, msg, out);
	}
	
}
