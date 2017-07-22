package org.bdc.dcm.comm.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class WindowFixCheckInterceptorImpl implements WindowFixCheckInterceptor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 数据帧 索引
	 */
	private int srcIndex = 0;

	private int lenFieldLen;
	
	private int width;
	
	/**
	 * 
	 * @param srcIndex 数据帧 绝对偏移量
	 * @param lenFieldLen
	 */
	public WindowFixCheckInterceptorImpl(int srcIndex, int lenFieldLen) {
		super();
		this.srcIndex = srcIndex;
		this.lenFieldLen = lenFieldLen;
	}
	
	@Override
	public boolean invoke(ByteBuf in) {
		if(srcIndex > in.readerIndex())return false;
		width = in.getByte(srcIndex)&0xff;
		int readerIndex = in.readerIndex();
		boolean flag = width ==  readerIndex  - srcIndex -lenFieldLen;
		if(!flag) {
			logger.error("长度出错");
		}
		return  flag;
	}
	@Override
	public int getWidth() {
		int wSize = width;
		width = 0;
		return wSize;
	}
}
