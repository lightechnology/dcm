package org.bdc.dcm.comm.callback;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class CrcInterceptor implements CheckInterceptor {

	@Override
	public boolean invoke(ByteBuf in) {
		int len = in.readableBytes();
		byte[] bs = new byte[len];
		in.readBytes(bs);
		Public.crc16_A001(bs);
		//TODO 继续写逻辑
		return false;
	}

}
