package org.bdc.dcm.comm.callback;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class CrcInterceptor implements SaveLastIndexInterceptor {

	@Override
	public boolean invoke(ByteBuf in) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			for(;in.isReadable();) {
				out.write(in.readByte());
				if(Public.bytes2Int(Public.crc16_A001(out.toByteArray())) == 0) {
					out.close();
					return true;
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
