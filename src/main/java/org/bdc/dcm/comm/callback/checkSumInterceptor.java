package org.bdc.dcm.comm.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.buffer.ByteBuf;

/**
 * 校验和可能重复
 * @author Administrator
 *
 */
public class checkSumInterceptor implements SaveLastIndexInterceptor {

	private static Logger logger = LoggerFactory.getLogger(checkSumInterceptor.class);
	/**
	 * 为了防止校验出错 也有可能算出来就是0 但是绝对不是-1
	 */
	private int sum = -1;
	
	public boolean invoke(ByteBuf in) {
		int checksum = 0;
		
		if(in.readableBytes() < 2) return false;
		
		if(sum == -1)
			sum = in.readByte()& 0xff;
			
		//自行维护readerIndex
		for (; in.isReadable();) {
			checksum = in.readByte() & 0xff;
			if (checksum == sum) {
				
				return true;
			}else// 当前计算的校验和还不到
				sum = (sum + checksum) & 0xff;
		}
		// 读完了都找不到校验和
		return false;
	}

	public void clearSum() {
		this.sum = -1;
	}
	
}
