package org.bdc.dcm.netty.framer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * <h3>错误信息字ERR</h3></br>
 * <table>
 * <tr>	
 * <td>Bit7</td><td>Bit6</td><td>Bit5</td><td>Bit4</td><td>Bit3</td><td>Bit2</td><td>Bit1</td><td>Bit0</td>
 * </tr>
 * <tr>
 * <td>保留</td><td>费率数超</td><td>日时段数超</td><td>年时区数超</td><td>通信速率不能更改</td><td>密码错/未授权</td><td>无请求数据</td><td>其他错误</td>
 * </tr>
 * </table>
 * @author Administrator
 *
 */
public class Gb_dlt645_2007FrameDecoder extends ByteToMessageDecoder  {

	public static Logger logger = LoggerFactory.getLogger(Gb_dlt645_2007FrameDecoder.class);
	
	public static final int YR_MAC_LEN = 6;
	
	public static final int ZJZD_MAC_LEN = YR_MAC_LEN;
	
	/**
	 * 编码输出多个 唤醒标志的个数
	 */
	public static final int writeWakUpFlagSize = 4;
	
	//public static byte[] VAILD_WAKEUP_FLAG = new byte[]{(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe}; 
	
	private static List<Byte> STATE_CODES = new ArrayList<>();
	private static List<Byte> ERROR_CODES = new ArrayList<>();
	
	static{
		STATE_CODES.addAll(Arrays.asList(new Byte[]{(byte)0x91,(byte)0xb1}));
		STATE_CODES.addAll(Arrays.asList(new Byte[]{(byte)0x92,(byte)0xb2}));
		STATE_CODES.addAll(Arrays.asList(new Byte[]{(byte)0x94,(byte)0x93,(byte)0x96,(byte)0x97,(byte)0x98,(byte)0x99,(byte)0x9A,(byte)0x9B}));
		ERROR_CODES.addAll(Arrays.asList(new Byte[]{(byte)0xd1,(byte)0xd2,(byte)0xd4,(byte)0x95,(byte)0xD6,(byte)0xD7,(byte)0xD8,(byte)0xD9,(byte)0xDA,(byte)0xDB}));
		
	}
	public static final byte WAKEUPFLAGBYTE = (byte) 0xfe;
	private static final byte STARTBYTE = (byte)0x68;
	
	private static final byte ENDBYTE = (byte)0x16;
	private byte[] yrMac;
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		decode(in, out);
	}
	
	public void decode(ByteBuf in, List<Object> out){
		if(in.readableBytes() == YR_MAC_LEN && yrMac == null){
			yrMac = new byte[YR_MAC_LEN];
			ByteBuf buf = in.alloc().buffer(YR_MAC_LEN);
			in.readBytes(yrMac);
			buf.writeBytes(yrMac);
			out.add(buf);
			return;
		}
		while(in.isReadable() && in.readableBytes() > 6){
			in.markReaderIndex();
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
				int startIndex = in.readerIndex();
				if(in.readByte() == STARTBYTE){
					byte[] zjzdMac = new byte[ZJZD_MAC_LEN];
					in.readBytes(zjzdMac);
					byte startByte = in.readByte();
					byte stateCode = in.readByte();
					if(startByte == STARTBYTE){
						
						
						if(STATE_CODES.contains(stateCode)){
							int dataLen = in.readByte() & 0xff;
							if(in.readableBytes() > dataLen){
								byte[] datas = new byte[dataLen];
								in.readBytes(datas);
								ByteBuf buf = buf(in, startIndex);
								byte checkSum = in.readByte();
								if((checkSum & 0xff) == checkSum(buf) && in.readByte() == ENDBYTE){
									out.add(buf(in, startIndex - wakeUpFlagSize - YR_MAC_LEN));
								}else
									reset(in);
							}else
								reset(in);
						}else if(ERROR_CODES.contains(stateCode)){
							int dataLen = in.readByte() & 0xff;
							if(in.readableBytes() > dataLen){
								byte[] datas = new byte[dataLen];
								in.readBytes(datas);
								ByteBuf buf = buf(in, startIndex);
								byte checkSum = in.readByte();
								if((checkSum & 0xff) == checkSum(buf) && in.readByte() == ENDBYTE){
									//标准错误码 直接打印 不做下一步
									if((stateCode & 0x01) == 0x01){
										logger.error("其他错误");
									}else if((stateCode & 0x02) == 0x02){
										logger.error("无请求数据");
									}else if((stateCode & 0x04) == 0x04){
										logger.error("密码错/未授权");
									}else if((stateCode & 0x08) == 0x08){
										logger.error("通信速率不能更改");
									}else if((stateCode & 0x10) == 0x10){
										logger.error("年时区数超");
									}else if((stateCode & 0x20) == 0x20){
										logger.error("日时段数超");
									}else if((stateCode & 0x40) == 0x40){
										logger.error("费率数超");
									}else if((stateCode & 0x80) == 0x80){
										logger.error("保留");
									}
								}else
									reset(in);
							}else
								reset(in);
						}
					}else
						reset(in);
				}else
					reset(in);
			} catch (Exception e){
				reset(in);
			}
		}
	}
	
	private ByteBuf buf(ByteBuf in, int startIndex) {
		int bufLen = in.readerIndex() - startIndex;
		ByteBuf buf = in.alloc().buffer(bufLen);
		in.getBytes(startIndex, buf, bufLen);
		return buf;
	}
	private int checkSum(ByteBuf in){
		int sum = 0;
		int max = in.readableBytes();
		for(int i=0;i<max;i++){
			sum = (sum + in.readByte()) & 0xff;
		}
		return sum;
	}
	private void reset(ByteBuf in){
		in.resetReaderIndex();
		in.readByte();
	}
}
