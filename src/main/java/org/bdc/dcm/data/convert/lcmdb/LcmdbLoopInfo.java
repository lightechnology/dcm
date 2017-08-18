package org.bdc.dcm.data.convert.lcmdb;

import org.bdc.dcm.netty.ChannelHandlerContextDecorator;


public class LcmdbLoopInfo {

	private ChannelHandlerContextDecorator ctx;
	
	private String mac;

	public LcmdbLoopInfo(ChannelHandlerContextDecorator ctx, String mac) {
		super();
		this.ctx = ctx;
		this.mac = mac;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public ChannelHandlerContextDecorator getCtx() {
		return ctx;
	}
	
	
}
