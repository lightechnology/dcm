package org.bdc.dcm.vo;

import org.bdc.dcm.netty.ChannelHandlerContextDecorator;


public class LoopInfo {

	private ChannelHandlerContextDecorator ctx;
	
	private String mac;

	public LoopInfo(ChannelHandlerContextDecorator ctx, String mac) {
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
