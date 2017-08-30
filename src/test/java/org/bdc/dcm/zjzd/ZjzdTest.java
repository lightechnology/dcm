package org.bdc.dcm.zjzd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.util.tools.Public;

public class ZjzdTest {

	/**
	 * 有人+硬件 数据帧：D8 B0 4C BD D1 47 FE FE FE FE 68 84 53 66 16 05 17 68 91 06 33 34 34 35 49 55 44 16
	 */
	@Test
	public void checkSum(){
		//String hex = "68 84 53 66 16 05 17 68 91 06 00 01 01 02 16 22";
		String hex = "68 84 53 66 16 05 17 68 91 06 33 34 34 35 49 55";
		byte[] bs = Public.hexString2bytes(hex);
		int sum = 0;
		for(byte b:bs) {
			sum=(sum+b)&0xff;
		}
		assertEquals((byte)0x44,sum);
		
	}
}
