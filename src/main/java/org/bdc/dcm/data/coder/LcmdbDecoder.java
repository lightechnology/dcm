package org.bdc.dcm.data.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import org.bdc.dcm.data.coder.comm.DataDecoderAdapter;
import org.bdc.dcm.utils.BaseTypeToBytesUtils;
import org.bdc.dcm.vo.DataModel;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataType;

import com.util.tools.Public;


public class LcmdbDecoder extends DataDecoderAdapter{

	private DataType dataType;

	public LcmdbDecoder(DataType dataType) {
		super(dataType);
		this.dataType = dataType;
	}

	@Override
	public void customProtocol(ChannelHandlerContext ctx, ByteBuf msg,Map<String,Object> tmpData) {
		tmpData.put("addr", msg.readByte());
	}

	@Override
	public DataPack knownProtocol(ChannelHandlerContext ctx, Map<String,DataModel> map,Map<String,Object> tmpData) {
		DataPack dataPack = new DataPack();
		String hex = Public.byte2hex(BaseTypeToBytesUtils.getBytes((long)map.get("mac").getVal()));
		dataPack.setMac(hex+"_"+tmpData.get("addr"));
		dataPack.setData(tmpData);
		return dataPack;
	}
	
}
