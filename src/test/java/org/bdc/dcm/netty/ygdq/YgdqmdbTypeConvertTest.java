package org.bdc.dcm.netty.ygdq;

import static org.junit.Assert.*;

import java.util.Random;

import org.bdc.dcm.netty.ygdqmdb.YgdqmdbTypeConvert;
import org.junit.Test;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import static org.bdc.dcm.data.coder.utils.CommUtils.*;
public class YgdqmdbTypeConvertTest {

	private Random random = new Random();
	@Test
	public void testDecode() throws Exception {
		YgdqmdbTypeConvert convert = new YgdqmdbTypeConvert();
		ByteBuf in = UnpooledByteBufAllocator.DEFAULT.buffer();
		
		int i = random.nextInt(9);
		byte[] bs = Public.int2Bytes(i, 2);
		reverse(bs);
		in.writeBytes(bs);
		assertEquals(i, convert.decode("Xs1", in));
		in.clear();
		
		i = random.nextInt(247);
		bs = Public.int2Bytes(i, 2);
		reverse(bs);
		in.writeBytes(bs);
		assertEquals(i, convert.decode("DZ", in));
		in.clear();
		
		in.writeBytes(new byte[]{0,0});
		assertEquals(9600, convert.decode("BUAD", in));
		in.clear();
		
		in.writeBytes(new byte[]{0,1});
		assertEquals(4800, convert.decode("BUAD", in));
		in.clear();
		
		in.writeBytes(new byte[]{0,2});
		assertEquals(2400, convert.decode("BUAD", in));
		in.clear();
		
		in.writeBytes(new byte[]{0,0});
		assertEquals(400, convert.decode("U.SCL", in));
		in.clear();
		
		in.writeBytes(new byte[]{0,1});
		assertEquals(100, convert.decode("U.SCL", in));
		in.clear();
		
		in.writeBytes(new byte[]{0,0});
		assertEquals(5, convert.decode("I.scl", in));
		in.clear();
		
		in.writeBytes(new byte[]{0,1});
		assertEquals(1, convert.decode("I.scl", in));
		in.clear();
		
		in.writeBytes(new byte[]{0,0});
		assertEquals("N.3.3", convert.decode("net", in));
		in.clear();
		
		in.writeBytes(new byte[]{0,1});
		assertEquals("N34", convert.decode("net", in));
		in.clear();
		
		i = random.nextInt(9999);
		bs = Public.int2Bytes(i, 2);
		reverse(bs);
		in.writeBytes(bs);
		assertEquals(i, convert.decode("PT", in));
		in.clear();
		
		i = random.nextInt(9999);
		bs = Public.int2Bytes(i, 2);
		reverse(bs);
		in.writeBytes(bs);
		assertEquals(i, convert.decode("CT", in));
		in.clear();
		
		float f = 123f;
		in.writeFloat(f);
		assertEquals(f, convert.decode("Ua", in));
		in.clear();

		in.writeFloat(f);
		assertEquals(f, convert.decode("Ub", in));
		in.clear();
		
		in.writeFloat(f);
		assertEquals(f, convert.decode("Uc", in));
		in.clear();
	}

}
