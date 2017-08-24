package org.bdc.dcm.data.coder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.coder.utils.CommUtils;
import org.bdc.dcm.data.convert.gb_dlt645_2007.GB_DLT645_2007TypeConvert;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.netty.framer.Gb_dlt645_2007FrameDecoder;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataType;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class Gb_dlt645_2007Decoder implements DataDecoder<ByteBuf> {
	
	private static int YR_MAC_LEN = Gb_dlt645_2007FrameDecoder.YR_MAC_LEN;
	
	private static int ZJZD_MAC_LEN = Gb_dlt645_2007FrameDecoder.ZJZD_MAC_LEN;
	
	public static int VAILD_WAKEUP_Len = Gb_dlt645_2007FrameDecoder.VAILD_WAKEUP_FLAG.length; 
	
	public static GB_DLT645_2007TypeConvert convert = GB_DLT645_2007TypeConvert.getConvert();

	/**
	 * 数据表示长度
	 */
	public static int DATA_INDENTITY_LEN = 4;
	
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, ByteBuf in) {

		byte[] yRMac = new byte[YR_MAC_LEN];
		in.readBytes(yRMac);
		in.readBytes(VAILD_WAKEUP_Len);
		in.readByte();//头
		byte[] zjzdMac = new byte[ZJZD_MAC_LEN];
		in.readBytes(zjzdMac);
		in.readByte();
		in.readByte();
		int totalDataLen = in.readByte() & 0xff;
		
		ByteBuf totalDataBuf = in.readBytes(totalDataLen);
		
		String mac = Public.byte2hex(yRMac)+" "+Public.byte2hex(zjzdMac);
		DataPack dataPack = CommUtils.getInitInfoDataPack(mac);
		
		StringBuffer sb = new StringBuffer();
		
		for(int i = 0;i < DATA_INDENTITY_LEN ; i++){
			sb.append(Public.byte2hex_ex(totalDataBuf.readByte()));
		}
		
		Map<String, Object> map= convert.decodeByReverseFieldAddr(sb.toString(), totalDataBuf);
	
		dataPack.setData(map);
		
		in.clear();
		
		return dataPack;
	}

}
