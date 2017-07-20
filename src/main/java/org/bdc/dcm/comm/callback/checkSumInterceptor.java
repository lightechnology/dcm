package org.bdc.dcm.comm.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

/**
 * 读bytebuf到校验和满足的位置
 * @author Administrator
 *
 */
public class checkSumInterceptor implements ZoneInterceptor {

	private static Logger logger = LoggerFactory.getLogger(checkSumInterceptor.class);
	private int readIndex;
	public boolean invoke(ByteBuf in) {
		int checksum = 0;
		int fullLen = 0;
		fullLen = in.readableBytes();
		for (int readLen = 0,sum = in.getByte(in.readerIndex()+readLen++) & 0xff; readLen < fullLen;) {
			checksum = in.getByte(in.readerIndex()+readLen) & 0xff;
			readLen++;
			if (checksum == sum){
				logger.error("checkSum：{},sum:{},readLen:{}",checksum,sum,readLen);
				readIndex = readLen;//补充最后一个checksum的读
				return true;
			}
			// 当前计算的校验和还不到
			sum = (sum + checksum) & 0xff;
		}
		// 读完了都找不到校验和
		return false;
	}

	@Override
	public int readIndex() {
		return readIndex;
	}
}
