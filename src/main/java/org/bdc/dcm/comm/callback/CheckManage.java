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
	
	private int windowSize = 0;
	
	public void addInterceptor(CheckInterceptor interceptor) {
		interceptors.add(interceptor);
	}
	public ByteBuf check(ByteBuf in, List<Object> out) {
		int firstReaderIndex = in.readerIndex();
		
		for(int i=lastInterceptorIndex;i<interceptors.size() && in.isReadable();i++){
			CheckInterceptor interceptor = interceptors.get(i);
			if(!interceptor.invoke(in)) {
	        	logger.error("xxxx,系统读索引:{},系统内容总长度:{},对应不通过拦截器{}",in.readerIndex(),in.readableBytes(),interceptor);
	        	if(interceptor instanceof SaveLastIndexInterceptor) {//读完还没有算出校验和
	        		if(lastInterceptorIndex != 0) //只记住第一个这类拦截器
	        			lastInterceptorIndex = i;//让下一帧能再次进来
	        		return in.alloc().buffer();
	        	}
	        	//头部不对或长度错误
	        	in.readerIndex(firstReaderIndex);
        		in.readByte();
        		lastInterceptorIndex = 0;
	        	return in.alloc().buffer();
			}else {//通过每个拦截器
				if(interceptor instanceof checkSumInterceptor) {
					int len = in.readerIndex() - firstReaderIndex ;
					if( windowSize > 0 && len < windowSize) {//不在窗口中
						logger.error("经过interceptor:{},计算的长度:{},与期望的窗口宽度：{}不匹配",interceptor,len,windowSize);
						lastInterceptorIndex = i;
						return in.alloc().buffer();
					}else {
						((checkSumInterceptor) interceptor).clearSum();
					}
				}else if(interceptor instanceof WindowFixCheckInterceptor) {//找出拦截器得出的最小~最大宽度
	        		windowSize = ((WindowFixCheckInterceptor) interceptor).getWidth();
	        	}
				lastInterceptorIndex = 0;
			}
		}
		
		//拦截器“都”运行成功-----------------------------
		int len = in.readerIndex() - firstReaderIndex;
		//窗口大小判断
		ByteBuf frame = in.alloc().buffer(len);
		frame.writeBytes(in,firstReaderIndex,len);
		
		lastInterceptorIndex = 0;
		windowSize = 0;
		return frame;
	}
}
