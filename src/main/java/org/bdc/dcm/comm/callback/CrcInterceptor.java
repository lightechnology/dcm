package org.bdc.dcm.comm.callback;


import io.netty.buffer.ByteBuf;

public class CrcInterceptor implements ZoneInterceptor {

	@Override
	public boolean invoke(ByteBuf in) {
		int fullLen = 0;
		fullLen = in.readableBytes();
		return false;
	}
}
