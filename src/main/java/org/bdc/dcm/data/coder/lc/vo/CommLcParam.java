package org.bdc.dcm.data.coder.lc.vo;

import io.netty.buffer.ByteBuf;

public class CommLcParam {

	private byte type;
	
	private ByteBuf pack;
	
	public CommLcParam(byte type,ByteBuf pack) {
		super();
		this.type = type;
		this.pack = pack;
	}

	public int getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}


	public ByteBuf getPack() {
		return pack;
	}

	public void setPack(ByteBuf pack) {
		this.pack = pack;
	}

	
	
	
}
