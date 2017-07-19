package org.bdc.dcm.comm.callback;

import io.netty.buffer.ByteBuf;

public class CheckCallBackAdapter implements CheckInterceptor {

	@Override
	public boolean checkSum(ByteBuf in) {
		return false;
	}

}
