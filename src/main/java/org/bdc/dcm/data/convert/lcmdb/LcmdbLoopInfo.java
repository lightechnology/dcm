package org.bdc.dcm.data.convert.lcmdb;

import io.netty.channel.ChannelHandlerContext;

public class LcmdbLoopInfo {

	private ChannelHandlerContext ctx;
	
	private String mac;

	public LcmdbLoopInfo(ChannelHandlerContext ctx, String mac) {
		super();
		this.ctx = ctx;
		this.mac = mac;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
	
}
