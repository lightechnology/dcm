package org.bdc.dcm.utils;

import static org.junit.Assert.*;

import org.bdc.dcm.comm.BaseTypeToBytesUtils;
import org.junit.Test;

import com.util.tools.Public;

public class BaseTypeToBytesUtilsTest {

	
	@Test
	public void testCharToBytes() {
		char c = 12;
		byte[] b = BaseTypeToBytesUtils.getBytes(c);
		String s = Public.byte2hex(b);
	}
	
	@Test
	public void testDoubleToBytes() {
		double d = 3.1;
		byte[] b = BaseTypeToBytesUtils.getBytes(d);
		
		
	}
 
	@Test
	public void testFloatToBytes() {
		String source = "00 12 4B 00 0A DC 89 5B ";
		byte[] bs = Public.hexString2bytes(source);
		float  f = BaseTypeToBytesUtils.getFloat(bs);
		byte[] fb = BaseTypeToBytesUtils.getBytes(f);
		assertEquals(source,Public.byte2hex(fb));
	}

	
	
	@Test
	public void testLongToBytes() {
		String source = "00 12 4B 00 0A DC 89 5B ";
		byte[] bs = Public.hexString2bytes(source);
		long l = Public.bytes2Long(bs);
		byte[] bs1 = BaseTypeToBytesUtils.getBytes(l);
		assertEquals(source,Public.byte2hex(bs1));
	}

	
}
