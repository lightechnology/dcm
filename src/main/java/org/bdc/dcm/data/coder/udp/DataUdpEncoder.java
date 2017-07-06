package org.bdc.dcm.data.coder.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.vo.DataPack;

public class DataUdpEncoder implements DataEncoder<DatagramPacket> {

	private DataEncoder<ByteBuf> dataEncoder;

	public DataUdpEncoder(DataEncoder<ByteBuf> dataEncoder) {
		this.dataEncoder = dataEncoder;
	}

	@Override
	public DatagramPacket package2Data(ChannelHandlerContext ctx, DataPack msg) {
		return new DatagramPacket(dataEncoder.package2Data(ctx, msg),
				(InetSocketAddress) msg.getSocketAddress());
	}

}
