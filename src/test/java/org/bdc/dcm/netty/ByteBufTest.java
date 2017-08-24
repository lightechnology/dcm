package org.bdc.dcm.netty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import junit.framework.Assert;

public class ByteBufTest {

	@Test
	public void testScpe() {
		ByteBuf in = UnpooledByteBufAllocator.DEFAULT.buffer(10);
		Assert.assertEquals(0, in.readableBytes());
		in.writeByte(1);
		Assert.assertEquals(1, in.readableBytes());
		in.clear();
		in.writeInt(1);
		Assert.assertEquals(4, in.readableBytes());
		in.clear();
	}
	@Test
	public void testGetBytes(){
		ByteBuf in = UnpooledByteBufAllocator.DEFAULT.buffer();
		in.writeBytes(new byte[]{1,2,3,4,5,6});
		ByteBuf in2 = UnpooledByteBufAllocator.DEFAULT.buffer();
		int len = 4;
		in.getBytes(0, in2, len);
		byte[] bs = new byte[len];
		in2.readBytes(bs);
		assertTrue(Arrays.equals(bs,new byte[]{1,2,3,4}));
	}
}
