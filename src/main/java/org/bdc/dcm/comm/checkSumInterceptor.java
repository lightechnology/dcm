package org.bdc.dcm.comm;

import org.bdc.dcm.comm.callback.CheckInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;

public class checkSumInterceptor implements CheckInterceptor{
	private static Logger logger = LoggerFactory.getLogger(checkSumInterceptor.class);
	/**
	 * 
	 * @param in
	 * @return 
	 */
	public boolean invoke(ByteBuf in){
        int checksum = 0;
        int fullLen = 0;
        fullLen = in.readableBytes();
        
        in.markReaderIndex();
        for(int readLen=1,sum = in.readByte()&0xff;readLen < fullLen;readLen++){
        	checksum = in.readByte()&0xff;
        	if(checksum == sum)
        		return true;
        	//当前计算的校验和还不到
        	sum = (sum + checksum)&0xff;
        }
        //读完了都找不到校验和
        return false;
	}
	@Override
	public ByteBuf getByteBuf(ByteBuf in) {
		return in;
	}
}
