package org.bdc.dcm.comm.callback;

import io.netty.buffer.ByteBuf;

public class FixBytesCheckInterceptor implements CheckInterceptor{

	private byte[] bytes;
	private boolean flag;
	/**
	 * 
	 * @param bytes
	 * @param flag true 本校验数据传递到下一步 , false 本校验数据不传递到下一步
	 */
	public FixBytesCheckInterceptor(byte[] bytes,boolean flag) {
		this.bytes = bytes;
		this.flag = flag;
	}

	public FixBytesCheckInterceptor(byte[] bytes) {
		super();
		this.bytes = bytes;
	}

	@Override
	public boolean invoke(ByteBuf in) {
		byte[] dst = new byte[bytes.length];
		in.readBytes(dst);
		return dst.equals(bytes);
	}

	@Override
	public ByteBuf getByteBuf(ByteBuf in) {
		return flag?in.alloc().buffer(in.readableBytes()).writeBytes(in):in.readBytes(bytes.length);
	}
}
