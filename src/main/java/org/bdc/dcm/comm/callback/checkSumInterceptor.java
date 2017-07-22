package org.bdc.dcm.comm.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.buffer.ByteBuf;

/**
 * 读bytebuf到校验和满足的位置
 * @author Administrator
 *
 */
public class checkSumInterceptor implements SaveLastIndexInterceptor {

	private static Logger logger = LoggerFactory.getLogger(checkSumInterceptor.class);
	public boolean invoke(ByteBuf in) {
		int checksum = 0;
		int fullLen = in.readableBytes();
		if(in.readableBytes() < 2) return false;
		//自行维护readerIndex
		for (int sum= in.readByte() & 0xff; in.isReadable();) {
			checksum = in.readByte() & 0xff;
			if (checksum == sum){
				logger.info("校验和通过的数据-------：系统数据总长度：{},系统读索引：{},当前正确数据长度：{}",fullLen,in.readerIndex());
				return true;
			}else{// 当前计算的校验和还不到
				sum = (sum + checksum) & 0xff;
			}
		}
		// 读完了都找不到校验和
		return false;
	}
	
}
