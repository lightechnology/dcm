package org.bdc.dcm.data.coder.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.util.tools.Public;

public class CommUtilsTest {

	@Test
	public void testReverse() {
		byte[] bs = Public.hexString2bytes("12345678");
		CommUtils.reverse(bs);
		assertEquals(Public.byte2hex(Public.hexString2bytes("78563412")),Public.byte2hex(bs));
	}

	@Test
	public void testCheckSum(){
		byte[] dst = new byte[]{1,2,3,4,5,6};
		assertEquals(7 , CommUtils.checkSum(dst, 2, 4));
		assertEquals(6 , CommUtils.checkSum(dst, 0, 3));
		assertEquals(11, CommUtils.checkSum(dst, 4, 6));
	}

}
