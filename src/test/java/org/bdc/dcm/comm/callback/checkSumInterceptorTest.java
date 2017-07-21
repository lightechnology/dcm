package org.bdc.dcm.comm.callback;

import static org.junit.Assert.*;

import org.junit.Test;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

public class checkSumInterceptorTest {

	@Test
	public void testInvoke() {
		CheckInterceptor interceptor = new checkSumInterceptor();
		ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
		ByteBuf in = alloc.buffer();
		in.writeBytes(Public.hexString2bytes("FE A5 01 2D 17 00 12 4B 00 0A DC 89 5B 00 01 03 1E 00 00 00 1E 00 00 25 80 00 00 00 00 00 00 00 CF 56 7601 2D 17 00 12 4B 00 0A DC 89 5B 00 01 03 1E 00 00 00 1E 00 00 25 80 00 00 00 00 00 00 00 CF"));
		in.setIndex(2, in.writerIndex());
		assertTrue(interceptor.invoke(in));
	}

}
