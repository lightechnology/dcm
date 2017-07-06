package org.bdc.dcm.data.coder.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.vo.DataPack;

public class DataUdpDecoder implements DataDecoder<DatagramPacket> {

	private DataDecoder<ByteBuf> dataDecoder;

	public DataUdpDecoder(DataDecoder<ByteBuf> dataDecoder) {
		this.dataDecoder = dataDecoder;
	}

	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, DatagramPacket msg) {
		DataPack dataPack = dataDecoder.data2Package(ctx, msg.content());
		dataPack.setSocketAddress(msg.sender());
		return dataPack;
	}

}
