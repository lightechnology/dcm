package org.bdc.dcm.data.coder;

import java.util.Map;

import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.coder.utils.CommUtils;
import org.bdc.dcm.data.convert.gb_dlt645_2007.GB_DLT645_2007TypeConvert;
import org.bdc.dcm.netty.framer.Gb_dlt645_2007FrameDecoder;
import org.bdc.dcm.vo.DataPack;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import static org.bdc.dcm.netty.framer.Gb_dlt645_2007FrameDecoder.YR_MAC_LEN;
import static org.bdc.dcm.netty.framer.Gb_dlt645_2007FrameDecoder.ZJZD_MAC_LEN;

public class Gb_dlt645_2007Decoder implements DataDecoder<ByteBuf> {
	
	public static byte WAKEUPFLAGBYTE = Gb_dlt645_2007FrameDecoder.WAKEUPFLAGBYTE; 
	
	public static GB_DLT645_2007TypeConvert convert = GB_DLT645_2007TypeConvert.getConvert();

	/**
	 * 数据表示长度
	 */
	public static int DATA_INDENTITY_LEN = 4;
	
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, ByteBuf in) {
		//纯 有人mac 包
		if(in.readableBytes() == YR_MAC_LEN){
			byte[] yrMac = new byte[YR_MAC_LEN];
			in.readBytes(yrMac);
			in.clear();
			return CommUtils.getInitInfoDataPack(Public.byte2hex(yrMac));
		}else{
			DataPack dataPack = null;
			try {
				byte[] yRMac = new byte[YR_MAC_LEN];
				in.readBytes(yRMac);

				//目测 有时返回2个 "0xfe" 有时候返回 4个 "0xfe"
				int readerIndex = in.readerIndex();
				int wakeUpFlagSize = 0;
				while(in.getByte(readerIndex++) == WAKEUPFLAGBYTE){
					wakeUpFlagSize++;
				}
				in.readBytes(wakeUpFlagSize);
				
				in.readByte();//头
				byte[] zjzdMac = new byte[ZJZD_MAC_LEN];
				in.readBytes(zjzdMac);
				in.readByte();
				in.readByte();
				int totalDataLen = in.readByte() & 0xff;
				
				ByteBuf totalDataBuf = in.readBytes(totalDataLen);
				
				String mac = Public.byte2hex(yRMac)+" "+Public.byte2hex(zjzdMac);
				dataPack = CommUtils.getInitInfoDataPack(mac);
				
				byte[] addr = new byte[DATA_INDENTITY_LEN];
				
				totalDataBuf.readBytes(addr);
				
				Map<String, Object> map= convert.decodeByDataStreamAddr(Public.byte2hex(addr), totalDataBuf);

				dataPack.setData(map);
				
				in.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return dataPack;
		}
		
	}
}
