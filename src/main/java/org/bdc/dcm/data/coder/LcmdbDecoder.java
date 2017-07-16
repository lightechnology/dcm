package org.bdc.dcm.data.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import org.bdc.dcm.data.coder.comm.DataDecoderAdapter;
import org.bdc.dcm.vo.DataModel;
import org.bdc.dcm.vo.e.DataType;


public class LcmdbDecoder extends DataDecoderAdapter{

	private DataType dataType;

	public LcmdbDecoder(DataType dataType) {
		super(dataType);
		this.dataType = dataType;
	}

	@Override
	public void customProtocol(ChannelHandlerContext ctx, ByteBuf msg) {
		
	}

	@Override
	public void knownProtocol(ChannelHandlerContext ctx, List<DataModel> dataModelList) {
		
	}
	
}
