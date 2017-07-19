package org.bdc.dcm.comm.callback;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;

public class CheckManage {

	private List<CheckInterceptor> interceptors = new ArrayList<>();
	
	public void addInterceptor(CheckInterceptor interceptor) {
		interceptors.add(interceptor);
	}
	public boolean check(ByteBuf in) {
		in.markReaderIndex();
		for(CheckInterceptor interceptor:interceptors) 
			if(!interceptor.invoke(interceptor.getByteBuf(in))) {
				in.resetReaderIndex();
	        	in.readByte();
				return false;
			} 
		return true;
	}
}
