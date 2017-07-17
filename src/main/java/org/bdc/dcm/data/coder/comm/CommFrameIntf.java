package org.bdc.dcm.data.coder.comm;

import java.util.List;
import java.util.Map;

import org.bdc.dcm.vo.DataModel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface CommFrameIntf {

	/**
	 * 
	 * @param ctx
	 * @param msg 解析不出的
	 * @param tmpData 临时变量处理
	 */
	public  void customProtocol(ChannelHandlerContext ctx, ByteBuf msg,Map<String,Object> tmpData);
	/**
	 * 
	 * @param ctx
	 * @param mapk
	 * @param tmpData 临时变量处理 
	 * @return
	 */
	public  void knownProtocol(ChannelHandlerContext ctx, Map<String,DataModel> map,Map<String,Object> tmpData, List<Object> out);
}
