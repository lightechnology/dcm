package org.bdc.ygdq;

import static org.junit.Assert.*;

import org.junit.Test;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;


public class NumUtils {

	@Test
	public void test() throws Exception {
		ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.buffer();
		//buf.writeFloat(213.4f);
		buf.writeBytes(Public.hexString2bytes("42 DD CC 80"));
		System.err.println(buf.readFloat());
		
	}

}
