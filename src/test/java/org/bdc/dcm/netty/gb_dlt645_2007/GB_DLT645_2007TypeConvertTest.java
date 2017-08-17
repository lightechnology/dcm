package org.bdc.dcm.netty.gb_dlt645_2007;

import static org.junit.Assert.*;

import org.bdc.dcm.data.coder.utils.CommUtils;
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
		GB_DLT645_2007TypeConvert convert = new GB_DLT645_2007TypeConvert();
		ByteBuf in = UnpooledByteBufAllocator.DEFAULT.buffer(10);
		
		byte[] bb = new byte[8];
		byte[] bs = new byte[]{(byte)0x94,0x00,0x00};
		for(int i=0;i<bs.length;i++){
			bb[i] = bs[i];
		}
		//yy年MM月dd日HH时mm分
		bb[3] = (byte)0x11;
		bb[4] = (byte)0x08;
		bb[5] = (byte)0x0f;
		bb[6] = (byte)0x11;
		bb[7] = (byte)0x11;
		in.writeBytes(bb);
		System.out.println("最终结果为："+convert.decode("a", in ));
	}
}
