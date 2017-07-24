package org.bdc.dcm.data.coder.intf;

import java.util.List;

import org.bdc.dcm.vo.DataPack;

import io.netty.channel.ChannelHandlerContext;

public interface DatasDecoder<I> extends DataDecoder<I>{

	public List<DataPack> datas2Package(ChannelHandlerContext ctx, I msg);
}
