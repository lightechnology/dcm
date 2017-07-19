package org.bdc.dcm.comm.callback;

import io.netty.buffer.ByteBuf;

public class FixBytesCheckInterceptor implements CheckInterceptor{

	private byte[] bytes;
	
	public FixBytesCheckInterceptor(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public boolean invoke(ByteBuf in) {
		byte[] dst = new byte[bytes.length];
		in.readBytes(dst);
		return dst.equals(bytes);
	}
}
