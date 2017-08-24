package org.bdc.dcm.data;

import static org.junit.Assert.*;

import org.bdc.dcm.data.convert.ConfigUtils;
import org.junit.Test;

public class ConfigUtilsTest {

	@Test
	public void testReverse() {
		assertEquals("78563412", ConfigUtils.reverse("12345678", 2));
	}

	@Test
	public void testCheckSum(){
		byte[] dst = new byte[]{1,2,3,4,5,6};
		assertEquals(7 , ConfigUtils.checkSum(dst, 2, 4));
		assertEquals(6 , ConfigUtils.checkSum(dst, 0, 3));
		assertEquals(11, ConfigUtils.checkSum(dst, 4, 6));
	}
}
