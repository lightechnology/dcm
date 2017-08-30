package org.bdc.dcm.data.convert.lqmdb;

import org.bdc.dcm.data.coder.intf.TypeConvert;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class LqmdbTypeConvert implements TypeConvert{

	private final static int DATATYPE_NOFOUND = -999;
	
	private final static int DATATYPE_2byteToBoolean = 0;
	
	private final static LqmdbTypeConvert convert = new LqmdbTypeConvert();
	
	private LqmdbTypeConvert() {};
	
	public static LqmdbTypeConvert getConvert() {
		return convert;
	}

	private int convertTypeStr2TypeId(String type) {
		if ("2byteToBoolean".equals(type)) {
			return DATATYPE_2byteToBoolean;
		}
		return DATATYPE_NOFOUND;
	}
	
	public Object decode(String type, ByteBuf in) {
		
		switch (convertTypeStr2TypeId(type)) {
			case DATATYPE_2byteToBoolean:
				byte[] bs = new byte[2];
				in.readBytes(bs);
				return Public.bytes2Int(bs) > 0;
			default:
				byte[] data = new byte[in.readableBytes()];
				in.readBytes(data);
				return null;
		}
	}
}
