package org.bdc.dcm.utils;

import java.lang.reflect.Method;
import org.junit.Test;
import junit.framework.TestCase;
import static  org.bdc.dcm.utils.CommTypeConvert.writeParmMapToBaseType;

public class CommTypeConvertTest extends TestCase {

	public static void write(int o) throws NoSuchMethodException, SecurityException{
			
	}
	public static void write(boolean o) throws NoSuchMethodException, SecurityException{
		
	}
	public static void write(char o) throws NoSuchMethodException, SecurityException{
		
	}
	public static void write(byte o) throws NoSuchMethodException, SecurityException{
		
	}
	public static void write(short o) throws NoSuchMethodException, SecurityException{
		
	}
	public static void write(long o) throws NoSuchMethodException, SecurityException{
		
	}
	public static void write(float o) throws NoSuchMethodException, SecurityException{
		
	}
	public static void write(double o) throws NoSuchMethodException, SecurityException{
		
	}
	public  boolean test2(Object o) throws NoSuchMethodException, SecurityException{
		Method m  = CommTypeConvertTest.class.getMethod("write", writeParmMapToBaseType(o));
		System.out.println("传入的参数："+o.getClass().getSimpleName()+",使用的方法："+m.getName()+"["+m.getParameterTypes()[0]+"]");
		return writeParmMapToBaseType(o).equals(m.getParameterTypes()[0]);
	}

	public static byte[] int2Bytes(int iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}
	@Test
	public void testPackagingGroupToBase() throws Exception {
		int v1 = 1;
		boolean v2 = true;
		char v3 = 1;
		byte v4 = 1;
		short v5 = 1;
		long v6= 1;
		float v7 = 1;
		double v8 = 1.1;
		assertEquals(true, test2(v1));
		assertEquals(true, test2(v2));
		assertEquals(true, test2(v3));
		assertEquals(true, test2(v4));
		assertEquals(true, test2(v5));
		assertEquals(true, test2(v6));
		assertEquals(true, test2(v7));
		assertEquals(true, test2(v8));
	}
	
}
