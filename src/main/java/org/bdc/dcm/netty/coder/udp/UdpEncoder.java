package org.bdc.dcm.netty.coder.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.data.log.DatagramPacket4Log;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.coder.Encoder;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;

public abstract class UdpEncoder extends Encoder<DataPack, DatagramPacket> {
	
	private SocketAddress remoteAddress;
	
	public UdpEncoder(Logger logger, NettyBoot nettyBoot, DataEncoder<DatagramPacket> encoder) {
		super(new DatagramPacket4Log(TAG, logger), nettyBoot, encoder);
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, DataPack msg, List<Object> out) {
		InetSocketAddress socketAddress = (InetSocketAddress) (null != remoteAddress ? remoteAddress
				: msg.getSocketAddress());
		msg.setSocketAddress(socketAddress);
		doEncode(ctx, msg, out);
	}

	public void setRemoteAddress(SocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

}
