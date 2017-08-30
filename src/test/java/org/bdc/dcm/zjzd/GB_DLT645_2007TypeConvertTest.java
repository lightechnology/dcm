package org.bdc.dcm.zjzd;

import org.bdc.dcm.data.convert.gb_dlt645_2007.GB_DLT645_2007TypeConvert;
import org.junit.Test;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class GB_DLT645_2007TypeConvertTest {

	public static int toInt(byte[] bRefArr) {
	    int iOutcome = 0;
	    byte bLoop;

	    for (int i = 0; i < bRefArr.length; i++) {
	        bLoop = bRefArr[i];
	        iOutcome += (bLoop & 0xFF) << (8 * i);
	    }
	    return iOutcome;
	}
	
	@Test
	public void test() {
		GB_DLT645_2007TypeConvert convert = GB_DLT645_2007TypeConvert.getConvert();
		ByteBuf in = UnpooledByteBufAllocator.DEFAULT.buffer(2);
		byte[] bs = Public.hexString2bytes("c6 54");
		in.writeBytes(bs);
		System.out.println("最终结果为："+convert.decode("a", in ));
	}
}
