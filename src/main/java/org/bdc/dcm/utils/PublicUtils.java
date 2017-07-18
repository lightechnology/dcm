package org.bdc.dcm.utils;

import io.netty.buffer.ByteBuf;

public class PublicUtils {

	/**
	 * 
	 * @param in
	 * @return crc校验和对的可读数据长度
	 */
	public static int crcCheckSum(ByteBuf in){
		int sum = 0;
        int checksum = 0;
        int runNum = 0;
    	byte f = in.readByte();
        sum = f&0xff;
        f = in.readByte();
        checksum = f & 0xff;
        for(runNum=0;checksum != (sum&0xff) && 0 < in.readableBytes();runNum++){
        	sum = (sum + checksum) & 0xff;
        	checksum = in.readByte() & 0xff;
        }
        if(checksum != sum)
        	runNum = 0;
        return runNum;
	}
}
