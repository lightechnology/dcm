package org.bdc.dcm.comm.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class LenCheckInterceptor implements CheckInterceptor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 数据帧 绝对偏移量
	 */
	private int lenFirstByteOffset = 0;

	private int lastLen = 0;

	private int lenFieldLen;
	/**
	 * 
	 * @param lenFirstByteOffset 数据帧 绝对偏移量
	 * @param lastLen
	 * @param lenFieldLen
	 */
	public LenCheckInterceptor(int lenFirstByteOffset, int lastLen, int lenFieldLen) {
		super();
		this.lenFirstByteOffset = lenFirstByteOffset;
		this.lastLen = lastLen;
		this.lenFieldLen = lenFieldLen;
	}
	/**
	 * 
	 * @param lenFirstByteOffset 数据帧 绝对偏移量
	 * @param lenFieldLen
	 */
	public LenCheckInterceptor(int lenFirstByteOffset, int lastLen) {
		super();
		this.lenFirstByteOffset = lenFirstByteOffset;
		this.lastLen = lastLen;
		this.lenFieldLen = 1;
	}
	
	@Override
	public boolean invoke(ByteBuf in) {
		int lenVal = in.getByte(lenFirstByteOffset)&0xff;
		int readerIndex = in.readerIndex();
		boolean flag = lenVal ==  readerIndex - lastLen - lenFirstByteOffset -lenFieldLen;
		if(!flag)
			logger.error("长度出错");
		return  flag;
	}
}
