package org.bdc.dcm.data.coder.tcp;

import io.netty.channel.ChannelHandlerContext;

import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.vo.DataPack;

public class DataTcpDecoder<T> implements DataDecoder<T> {
	
	private DataDecoder<T> dataDecoder;

	public DataTcpDecoder(DataDecoder<T> dataDecoder) {
		this.dataDecoder = dataDecoder;
	}

	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, T msg) {
		DataPack dataPack = dataDecoder.data2Package(ctx, msg);
		dataPack.setSocketAddress(ctx.channel().remoteAddress());
		return dataPack;
	}

}
