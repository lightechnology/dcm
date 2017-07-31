package org.bdc.dcm.lc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class DataGernator {
	
	private  static Logger logger = LoggerFactory.getLogger(DataGernator.class);
	public static String writeMacPack(String mac) {
		String fix = "FF A5 FF 09 0C ";
		int sum = 0;
		byte[] bs = Public.hexString2bytes(fix+mac);
		for(byte b:bs) {
			sum=(sum+b)&0xff;
		}
		
		String result = fix + mac +" "+ Public.byte2hex_ex(Public.int2Bytes(sum, 1)[0]);
		return result;
	}
	/**
	 * 
	 * @param mac
	 * @param addr
	 * @param wendu 温度
	 * @return
	 */
	public static String writePackInfo(String mac,String addr) {
		String header = "Fe a5 ";
		String fixed = "01 2D 17 "+mac+" 00 ";
		String modbus = addr+" 03 1E 00 00 00 1E 00 01 1F 80 00 00 00 00 00 00 00 CF 57 E0 00 00 00 00 00 00 00 00 01 02 01 00";
		byte[] crc = Public.crc16_A001(Public.hexString2bytes(modbus));
		modbus += " "+Public.byte2hex_ex(crc[1])+" "+Public.byte2hex_ex(crc[0]);
		byte[] bs = Public.hexString2bytes(fixed+modbus);
		int sum = 0;
		for(byte b:bs) {
			sum=(sum+b)&0xff;
		}
		return header + fixed + modbus + " " +Public.byte2hex_ex(Public.int2Bytes(sum, 1)[0]);
	}
	public static ByteBuf buf(String mac,String addr) {
		PooledByteBufAllocator pooledByteBufAllocator  = PooledByteBufAllocator.DEFAULT;
		ByteBuf buf = pooledByteBufAllocator.buffer();
		String pack = DataGernator.writePackInfo("00 12 4B 00 0A DC 89 5B","01");
		return buf.writeBytes(Public.hexString2bytes(pack));
		
	}
}
