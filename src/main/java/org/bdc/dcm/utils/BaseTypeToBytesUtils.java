package org.bdc.dcm.utils;

/**
 * 基本类型转字节数组工具
 * @author 李哲弘
 *
 */
public class BaseTypeToBytesUtils {


	public static byte[] byteToBytes(byte b) {
		return new byte[] { b };
	}

	/**
	 * 将一个char字符转换位字节数组（2个字节），b[0]存储高位字符，大端
	 * 
	 * @param c
	 *            字符（java char 2个字节）
	 * @return 代表字符的字节数组
	 */
	public static byte[] charToBytes(char c) {
		byte[] b = new byte[8];
		b[0] = (byte) (c >>> 8);
		b[1] = (byte) c;
		return b;
	}

	/**
	 * 将一个双精度浮点数转换位字节数组（8个字节），b[0]存储高位字符，大端
	 * 
	 * @param d
	 *            双精度浮点数
	 * @return 代表双精度浮点数的字节数组
	 */
	public static byte[] doubleToBytes(double d) {
		return longToBytes(Double.doubleToLongBits(d));
	}

	/**
	 * 将一个浮点数转换为字节数组（4个字节），b[0]存储高位字符，大端
	 * 
	 * @param f
	 *            浮点数
	 * @return 代表浮点数的字节数组
	 */
	public static byte[] floatToBytes(float f) {
		return intToBytes(Float.floatToIntBits(f));
	}

	/**
	 * 将一个整数转换位字节数组(4个字节)，b[0]存储高位字符，大端
	 * 
	 * @param i
	 *            整数
	 * @return 代表整数的字节数组
	 */
	public static byte[] intToBytes(int i) {
		byte[] b = new byte[4];
		b[0] = (byte) (i >>> 24);
		b[1] = (byte) (i >>> 16);
		b[2] = (byte) (i >>> 8);
		b[3] = (byte) i;
		return b;
	}

	/**
	 * 将一个长整数转换位字节数组(8个字节)，b[0]存储高位字符，大端
	 * 
	 * @param l
	 *            长整数
	 * @return 代表长整数的字节数组
	 */
	public static byte[] longToBytes(long l) {
		byte[] b = new byte[8];
		b[0] = (byte) (l >>> 56);
		b[1] = (byte) (l >>> 48);
		b[2] = (byte) (l >>> 40);
		b[3] = (byte) (l >>> 32);
		b[4] = (byte) (l >>> 24);
		b[5] = (byte) (l >>> 16);
		b[6] = (byte) (l >>> 8);
		b[7] = (byte) (l);
		return b;
	}

	/**
	 * short到字节数组的转换.
	 */
	public static byte[] shortToBytes(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8;// 向右移8位
		}
		return b;
	}
	/**
	 * boolean到字节数组的转换
	 * @param b
	 * @return
	 */
	public static byte[] booleanToBytes(boolean b){
		return new byte[]{b?(byte) 0x01:(byte)0x00};
	}
}
