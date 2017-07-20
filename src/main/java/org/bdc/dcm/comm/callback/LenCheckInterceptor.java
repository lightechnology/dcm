package org.bdc.dcm.comm.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class LenCheckInterceptor implements CheckInterceptor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private int lenFirstByteOffset = 0;

	private int lastLen = 0;

	private int lenFieldLen;

	


	public LenCheckInterceptor(int lenFirstByteOffset, int lastLen, int lenFieldLen) {
		super();
		this.lenFirstByteOffset = lenFirstByteOffset;
		this.lastLen = lastLen;
		this.lenFieldLen = lenFieldLen;
	}

	public LenCheckInterceptor(int lenFirstByteOffset, int lastLen) {
		super();
		this.lenFirstByteOffset = lenFirstByteOffset;
		this.lastLen = lastLen;
		this.lenFieldLen = 1;
	}
	/**
	 * @param in
	 */
	@Override
	public boolean invoke(ByteBuf in) {
		byte[] lenBytes = new byte[lenFieldLen];
		in.getBytes(in.readerIndex() + lenFirstByteOffset, lenBytes);
		int lenVal = Public.bytes2Int(lenBytes);
		return lenVal == in.readableBytes() - lastLen - lenFieldLen - lenFirstByteOffset;
	}
}
