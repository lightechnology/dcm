package org.bdc.dcm.netty.framer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class Gb_dlt645_2007FrameDecoder extends ByteToMessageDecoder  {

	public static int YR_MAC_LEN = 6;
	
	public static int ZJZD_MAC_LEN = YR_MAC_LEN;
	
	public static byte[] VAILD_WAKEUP_FLAG = new byte[]{(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe}; 
	
	private static List<Byte> STATE_CODES = new ArrayList<>();
	private static List<Byte> ERROR_CODES = new ArrayList<>();
	
	static{
		STATE_CODES.addAll(Arrays.asList(new Byte[]{(byte)0x91,(byte)0xb1}));
		STATE_CODES.addAll(Arrays.asList(new Byte[]{(byte)0x92,(byte)0xb2}));
		STATE_CODES.addAll(Arrays.asList(new Byte[]{(byte)0x94,(byte)0x93,(byte)0x96,(byte)0x97,(byte)0x98,(byte)0x99,(byte)0x9A,(byte)0x9B}));
		ERROR_CODES.addAll(Arrays.asList(new Byte[]{(byte)0xd1,(byte)0xd2,(byte)0xd4,(byte)0x95,(byte)0xD6,(byte)0xD7,(byte)0xD8,(byte)0xD9,(byte)0xDA,(byte)0xDB}));
		
	}
	
	private static final byte STARTBYTE = (byte)0x68;
	
	private static final byte ENDBYTE = (byte)0x16;
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		decode(in, out);
	}
	
	public void decode(ByteBuf in, List<Object> out){
		while(in.isReadable() && in.readableBytes() > 6){
			in.markReaderIndex();
			try {
				byte[] yRMac = new byte[YR_MAC_LEN];
				in.readBytes(yRMac);
				byte[] wakeUpFlag = new byte[VAILD_WAKEUP_FLAG.length];
				in.readBytes(wakeUpFlag);
				int startIndex = in.readerIndex();
				if(Arrays.equals(wakeUpFlag, VAILD_WAKEUP_FLAG) && in.readByte() == STARTBYTE){
					byte[] zjzdMac = new byte[ZJZD_MAC_LEN];
					in.readBytes(zjzdMac);
					byte startByte = in.readByte();
					byte stateCode = in.readByte();
					if(startByte == STARTBYTE && (STATE_CODES.contains(stateCode) || ERROR_CODES.contains(stateCode))){
						int dataLen = in.readByte() & 0xff;
						if(in.readableBytes() > dataLen){
							byte[] datas = new byte[dataLen];
							in.readBytes(datas);
							ByteBuf buf = buf(in, startIndex);
							byte checkSum = in.readByte();
							if((checkSum & 0xff) == checkSum(buf) && in.readByte() == ENDBYTE){
								out.add(buf(in, startIndex - VAILD_WAKEUP_FLAG.length - YR_MAC_LEN));
							}else
								reset(in);
						}else
							reset(in);
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
