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
	/**
	 * 
	 * @param lenFirstByteOffset 长度首字节偏移量 
	 * @param lenFieldLen 长度标识的字节长度
	 * @param lastLen 忽略的最后字节数
	 */
	public LenCheckInterceptor(int lenFirstByteOffset,int lenFieldLen,int lastLen) {
		this.lenFirstByteOffset = lenFirstByteOffset;
		this.lastLen = lastLen;
		this.lenFieldLen = lenFieldLen;
	}
	/**
	 * 默认长度标识为1个字节
	 * @param lenFirstByteOffset 长度首字节偏移量 
	 * @param lastLen 忽略的最后字节数
	 */
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
	public boolean checkSum(ByteBuf in) {
		byte [] lenBytes = new byte[lenFieldLen];
		in.getBytes(lenFirstByteOffset, lenBytes);
		int lenVal = Public.bytes2Int(lenBytes);
		return lenVal  == in.readableBytes() - lastLen - lenFieldLen - lenFirstByteOffset;
	}

}
