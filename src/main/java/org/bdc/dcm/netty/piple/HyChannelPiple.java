package org.bdc.dcm.netty.piple;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

/**
 * 如果需要请自行扩展 不要返回能操作 写 的 避免上下水位失效
 * @author Administrator
 *
 */
public class HyChannelPiple {

	private ChannelPipeline pipeline;

	public HyChannelPiple(ChannelPipeline pipeline) {
		this.pipeline = pipeline;
	}
	public void atLast(String name, ChannelHandler handler){
		pipeline.addLast(name, handler);
	}
}
