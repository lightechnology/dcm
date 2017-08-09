package org.bdc.modbus;

import com.util.tools.Public;

public class CrcGernator {

	public static void main(String[] args) {
		byte[] crc;
		for(byte i=0;i<20;i++){
			byte[] bs = new byte[]{i,3,0,(byte)0x12,0,6};
			crc = Public.crc16_A001(bs);
			
			System.err.println(Public.byte2hex(bs)+" "+Public.byte2hex(crc));
		}
	}
}
