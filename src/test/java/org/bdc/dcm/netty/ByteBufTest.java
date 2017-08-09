package org.bdc.dcm.netty;

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
}
