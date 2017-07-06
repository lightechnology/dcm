package org.bdc.dcm.data.coder.tcp;

import io.netty.channel.ChannelHandlerContext;

import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.vo.DataPack;

public class DataTcpEncoder<T> implements DataEncoder<T> {
	
	private DataEncoder<T> dataEncoder;

	public DataTcpEncoder(DataEncoder<T> dataEncoder) {
		this.dataEncoder = dataEncoder;
	}

	@Override
	public T package2Data(ChannelHandlerContext ctx, DataPack msg) {
		return dataEncoder.package2Data(ctx, msg);
	}

}
