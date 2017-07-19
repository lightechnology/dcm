package org.bdc.dcm.comm;

import java.util.ArrayList;
import java.util.List;

import org.bdc.dcm.comm.callback.CheckInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;

public class CheckService {
	private static Logger logger = LoggerFactory.getLogger(CheckService.class);
	/**
	 * 
	 * @param in
	 * @return 
	 * @return crc校验和对的可读数据长度
	 */
	public static boolean crcCheckSum(ByteBuf in,List<CheckInterceptor> cbs){
        int checksum = 0;
        int fullLen = 0;
        fullLen = in.readableBytes();
        
        if(cbs == null) cbs = new ArrayList<>();//没有校验拦截器 那么直接空通过
        in.markReaderIndex();
        for(int readLen=1,sum = in.readByte()&0xff;readLen < fullLen;readLen++){
        	checksum = in.readByte()&0xff;
        	if(checksum == sum){
        		ByteBuf buf = in.resetReaderIndex().readBytes(readLen+1);
        		for(CheckInterceptor cb:cbs){
        			if(!cb.checkSum(buf.alloc().buffer(buf.readableBytes()).writeBytes(buf))){
        				return false;//只要在一系列callback有一个出错 那么检验不通过
        			}
        		}
        		//一些列检验通过
        		return true;
        	}
        	//当前计算的校验和还不到
        	sum = (sum + checksum)&0xff;
        }
        //读完了都找不到校验和
        return false;
	}
}
