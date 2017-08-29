package org.bdc.dcm.data.coder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.data.convert.gb_dlt645_2007.GB_DLT645_2007TypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataPackType;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import static org.bdc.dcm.netty.framer.Gb_dlt645_2007FrameDecoder.YR_MAC_LEN;
import static org.bdc.dcm.netty.framer.Gb_dlt645_2007FrameDecoder.ZJZD_MAC_LEN;

public class Gb_dlt645_2007Encoder implements DataEncoder<ByteBuf>  {
	
	public static GB_DLT645_2007TypeConvert convert = GB_DLT645_2007TypeConvert.getConvert();
	
	@Override
	@SuppressWarnings("unchecked")
	public ByteBuf package2Data(ChannelHandlerContext ctx, DataPack msg) {
		
		ByteBuf src = null;
		
		DataPackType dataPackType = msg.getDataPackType();
		String indentity = "";
		if(dataPackType.equals(DataPackType.Cmd)) 
			indentity = msg.getToMac(); 
		else if(dataPackType.equals(DataPackType.Info)) 
			indentity = msg.getMac();
		
		byte[] macBytes = Public.hexString2bytes(indentity);
		byte[] zjzdMac = new byte[ZJZD_MAC_LEN];
		System.arraycopy(macBytes, YR_MAC_LEN, zjzdMac, 0, ZJZD_MAC_LEN);
		Map<String,Object> data = msg.getData();
		Iterator<String> keys = data.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			try {
				byte[] out = convert.encoder(key, null, zjzdMac);
				if(out.length > 0){
					return ctx.alloc().buffer(out.length).writeBytes(out);
				}else
					return src;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return src;
	}

}
