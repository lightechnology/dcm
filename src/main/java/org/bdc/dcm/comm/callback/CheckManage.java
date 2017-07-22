package org.bdc.dcm.comm.callback;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.buffer.ByteBuf;

public class CheckManage {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private List<CheckInterceptor> interceptors = new ArrayList<>();

	private int lastInterceptorIndex = 0;
	
	public void addInterceptor(CheckInterceptor interceptor) {
		interceptors.add(interceptor);
	}
	public void check(ByteBuf in, List<Object> out) {
		int firstReaderIndex = in.readerIndex();
		for(int i=lastInterceptorIndex;i<interceptors.size();i++){
			CheckInterceptor interceptor = interceptors.get(i);
			if(!interceptor.invoke(in)) {
	        	logger.error("xxxx,系统读索引:{},系统内容总长度:{},对应不通过拦截器{}",in.readerIndex(),in.readableBytes(),interceptor);
	        	if(interceptor instanceof SaveLastIndexInterceptor) {//读完还没有算出校验和
	        		if(lastInterceptorIndex != 0) //只记住第一个这类拦截器
	        			lastInterceptorIndex = i;//让下一帧能再次进来
	        		return;
	        	}
	        	//头部不对或长度错误
	        	in.readerIndex(firstReaderIndex);
        		in.readByte();
        		lastInterceptorIndex = 0;
	        	return ;
			}
			lastInterceptorIndex = 0;
		}
		//拦截器“都”运行成功-----------------------------
		int len = in.readerIndex() - firstReaderIndex;
		ByteBuf frame = in.alloc().buffer(len);
		frame.writeBytes(in,firstReaderIndex,len);
		out.add(frame);
	}
}
