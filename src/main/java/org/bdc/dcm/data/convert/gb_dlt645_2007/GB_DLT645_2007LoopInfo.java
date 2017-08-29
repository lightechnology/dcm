package org.bdc.dcm.data.convert.gb_dlt645_2007;

import java.util.ArrayList;
import java.util.List;

import org.bdc.dcm.netty.ChannelHandlerContextDecorator;
import org.bdc.dcm.vo.LoopInfo;

import io.netty.buffer.ByteBuf;

public class GB_DLT645_2007LoopInfo extends LoopInfo {
	
	/**
	 * 固定任务列表
	 */
	List<byte[]> bufs = new ArrayList<>();
	
	public GB_DLT645_2007LoopInfo(ChannelHandlerContextDecorator ctx, String mac) {
		super(ctx, mac);
	}
	
}
