package org.bdc.dcm.comm.callback;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.buffer.ByteBuf;

public class CheckManage {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<CheckInterceptor> interceptors = new ArrayList<>();

	public void addInterceptor(CheckInterceptor interceptor) {
		interceptors.add(interceptor);
	}
	public synchronized boolean check(ByteBuf in) {
		in.markReaderIndex();
		int readIndexSum = 0;
		for(CheckInterceptor interceptor:interceptors){
			if(!interceptor.invoke(in)) {
				in.resetReaderIndex();
	        	in.readByte();
				return false;
			}else{
				if(interceptor instanceof ZoneInterceptor){
					ZoneInterceptor zone = (ZoneInterceptor) interceptor;
					readIndexSum+=zone.readIndex();
				}
			}
		}
		in = in.readBytes(readIndexSum);
		return true;
	}
}
