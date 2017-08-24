package org.bdc.dcm.netty.framer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class Gb_dlt645_2007FrameDecoderTest {

	private Gb_dlt645_2007FrameDecoder frame = new Gb_dlt645_2007FrameDecoder();
	@Test
	public void testDecodeByteBufListOfObject() {
	
		ByteBuf vaildIn = UnpooledByteBufAllocator.DEFAULT.buffer();
		vaildIn.writeBytes(Public.hexString2bytes("D8 B0 4C BD D1 47 FE FE FE FE 68 84 53 66 16 05 17 68 91 06 33 34 34 35 49 55 44 16"));
		
		String[] cmds = new String[]{
				"D8 B0 4C BD D1 47 F1 FE FE FE 68 84 53 66 16 05 17 68 91 06 33 34 34 35 49 55 44 16",//×
				"D8 B0 4C BD D1 47 FE F2 FE FE 68 84 53 66 16 05 17 68 91 06 33 34 34 35 49 55 44 16",//×
				"D8 B0 4C BD D1 47 FE FE F3 FE 68 84 53 66 16 05 17 68 91 06 33 34 34 35 49 55 44 16",//×
				"D8 B0 4C BD D1 47 FE FE FE F4 68 84 53 66 16 05 17 68 91 06 33 34 34 35 49 55 44 16",//×
				"D8 B0 4C BD D1 47 FE FE FE FE 63 84 53 66 16 05 17 68 91 06 33 34 34 35 49 55 44 16",//×
				"D8 B0 4C BD D1 47 FE FE FE FE 68 84 53 66 16 05 17 62 91 06 33 34 34 35 49 55 44 16",//×
				"D8 B0 4C BD D1 47 FE FE FE FE 68 84 53 66 16 05 17 68 91 06 33 34 34 35 49 55 44 15",//×
				"D8 B0 4C BD D1 47 FE FE FE FE 68 84 53 66 16 05 17 68 91 06 33 34 34 35 49 55 44 16" //√
				};
		for(int i=0;i<cmds.length;i++){
			List<Object> list = new ArrayList<>();
			ByteBuf in = UnpooledByteBufAllocator.DEFAULT.buffer();
			in.writeBytes(Public.hexString2bytes(cmds[i]));
			long startTime = System.currentTimeMillis();
			frame.decode(in, list);
			if(list.size() > 0){
				long diff = System.currentTimeMillis() - startTime;
				ByteBuf out = (ByteBuf) list.get(0);
				assertTrue(out.equals(vaildIn) && diff < 1);
			}
		}
	}

}
