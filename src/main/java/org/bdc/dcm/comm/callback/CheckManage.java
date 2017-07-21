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
	public ByteBuf check(ByteBuf in) {
		in.markReaderIndex();
		for(int i=0;i<interceptors.size();i++){
			CheckInterceptor interceptor = interceptors.get(i);
			if(!interceptor.invoke(in)) {
	        	logger.error("xxxx,系统读索引:{},系统内容总长度:{},对应不通过拦截器{}",in.readerIndex(),in.readableBytes(),interceptor);
	        	in.readByte();//拦截器中没有影响 bytebuf的数据读索引
	        	return null;
			}
		}
		in.resetReaderIndex();
		return in;
	}
}
