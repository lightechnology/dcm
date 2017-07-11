package org.bdc.dcm.netty.lc;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;


public class LcTypeConvert{

	private static Logger logger = LoggerFactory.getLogger(LcTypeConvert.class);
	
	private final static int DATATYPE_REMAININGTIMELONG = 0;
	private final static int DATATYPE_REMAININGELECTRICITY = 1;
	private final static int DATATYPE_TOTALTIME = 2;
	private final static int DATATYPE_TOTALACTIVEPOWER = 3;
	private final static int DATATYPE_U = 4;
	private final static int DATATYPE_I = 5;
	private final static int DATATYPE_P = 6;
	private final static int DATATYPE_COS$ = 7;
	private final static int DATATYPE_JDQTIAOZA = 8;
	private final static int DATATYPE_TEMPERATURE = 9;
	private final static int DATATYPE_JDQSTATE = 10;
	private final static int DATATYPE_POWEROFFUNLOCK = 11;
	public static int convertTypeStr2TypeId(String type) {
		if ("remainTimeLong".equals(type)) {
			return DATATYPE_REMAININGTIMELONG;
		}
		if ("remainELectricity".equals(type)) {
			return DATATYPE_REMAININGELECTRICITY;
		}
		if ("totalTime".equals(type)) {
			return DATATYPE_TOTALTIME;
		}
		if ("totalActivePower".equals(type)) {
			return DATATYPE_TOTALACTIVEPOWER;
		}
		if ("u".equals(type)) {
			return DATATYPE_U;
		}
		if ("i".equals(type)) {
			return DATATYPE_I;
		}
		if ("p".equals(type)) {
			return DATATYPE_P;
		}
		if ("cos$".equals(type)) {
			return DATATYPE_COS$;
		}
		if ("JQD_TiaoZa".equals(type)) {
			return DATATYPE_JDQTIAOZA;
		}
		if ("temperature".equals(type)) {
			return DATATYPE_TEMPERATURE;
		}
		if ("JDQ_state".equals(type)) {
			return DATATYPE_JDQSTATE;
		}
		if ("powerOffUnlock".equals(type)) {
			return DATATYPE_POWEROFFUNLOCK;
		}
		return -1;
	}
	//不包括地址
	public static byte[] outModbusBytesByType(String type, Object val,byte modbusAddr){
		byte[] bs = null,tmp,crc;
		boolean isOpen;
		switch (convertTypeStr2TypeId(type)) {
			//温度 0 关机 22-30 则控制
			case DATATYPE_TEMPERATURE:
				int temperature = (Integer) val;
				//01 10 00 37 00 01 02 00 00 A2 17 57 
				bs = new byte[11];
				tmp = new byte[9];
				tmp[0] = modbusAddr;
				tmp[1] = (byte)0x10;//功能码
				tmp[2] = 00;
				tmp[3] = (byte)0x37;
				tmp[4] = 00;
				tmp[5] = 01;
				tmp[6] = 02;
				if(temperature == 0){
					tmp[7] = 0;
					tmp[8] = 0;//关闭标志位
				}else{
					tmp[7] = Public.int2Bytes(temperature, 1)[0];
					tmp[8] = 03;//制冷标志位
				}
				crc = Public.crc16_A001(tmp);
				for(int i=0;i<tmp.length;i++)
					bs[i] = tmp[i];
				bs[9] = crc[1];
				bs[10] = crc[0];
				return bs;
				
			//继电器状态	
			case DATATYPE_JDQSTATE:
				isOpen = (boolean) val;
				bs = new byte[8];
				tmp = new byte[6];
				tmp[0] = modbusAddr;
				tmp[1] = 05;
				tmp[2] = 00;
				tmp[3] = 00;
				tmp[4] = isOpen?00:(byte)0xFF;
				tmp[5] = 00;
				crc = Public.crc16_A001(tmp);
				for(int i=0;i<tmp.length;i++)
					bs[i] = tmp[i];
				bs[6] = crc[1];
				bs[7] = crc[0];
				return bs;
			//断电解锁	
			case DATATYPE_POWEROFFUNLOCK:
				isOpen = (boolean) val;
				bs = new byte[11];
				tmp = new byte[9];
				tmp[0] = modbusAddr;
				tmp[1] = (byte)0x10;//功能码
				tmp[2] = 00;
				tmp[3] = (byte)0x16;
				tmp[4] = 00;
				tmp[5] = 01;
				tmp[6] = 02;
				tmp[7] = isOpen?(byte)0x01:(byte)0x00;
				tmp[8] = 00;
				crc = Public.crc16_A001(tmp);
				for(int i=0;i<tmp.length;i++)
					bs[i] = tmp[i];
				bs[9] = crc[1];
				bs[10] = crc[0];
				return bs;
			default:
				return bs;
		}
	}
	public static Object convertByteBuf2TypeValue(String type, ByteBuf in) {
		byte[] data;
		byte[] dataByte;
		in.markReaderIndex();
		switch (convertTypeStr2TypeId(type)) {
			case DATATYPE_REMAININGTIMELONG:
			
				data = new byte[4];
				in.readBytes(data);
				in.resetReaderIndex();
				if(logger.isDebugEnabled())
					logger.debug("剩余用电时间:{}",Public.byte2hex(data));
				return Double.valueOf(in.readInt())/10;
				
			case DATATYPE_REMAININGELECTRICITY:
				data = new byte[4];
				in.readBytes(data);
				in.resetReaderIndex();
				if(logger.isDebugEnabled())
					logger.debug("剩余电量:{}",Public.byte2hex(data));
				return Double.valueOf(in.readInt())/3200;
				
			case DATATYPE_TOTALTIME:
				data = new byte[4];
				in.readBytes(data);
				in.resetReaderIndex();
				if(logger.isDebugEnabled())
					logger.debug("总用电时间:{}",Public.byte2hex(data));
				return Double.valueOf(in.readInt())/10;
				
			case DATATYPE_TOTALACTIVEPOWER:
				data = new byte[4];
				in.readBytes(data);
				in.resetReaderIndex();
				if(logger.isDebugEnabled())
					logger.debug("总有功电能:{}",Public.byte2hex(data));
				return Double.valueOf(in.readInt())/3200;
				
			case DATATYPE_U:
				
				dataByte = new byte[2];
				in.readBytes(dataByte);
				reverse(dataByte);
				if(logger.isDebugEnabled())
					logger.debug("电压:{}",Public.byte2hex(dataByte));
				return Double.valueOf(Public.bytes2Int(dataByte))/100;
				
			case DATATYPE_I:
				dataByte = new byte[2];
				in.readBytes(dataByte);
				reverse(dataByte);
				if(logger.isDebugEnabled())
					logger.debug("电流:{}",Public.byte2hex(dataByte));
				return Double.valueOf(Public.bytes2Int(dataByte))/1000;
				
			case DATATYPE_P:
				dataByte = new byte[2];
				in.readBytes(dataByte);
				reverse(dataByte);
				if(logger.isDebugEnabled())
					logger.debug("功率:{}",Public.byte2hex(dataByte));
				return Double.valueOf(Public.bytes2Int(dataByte));
				
			case DATATYPE_COS$:
				dataByte = new byte[2];
				in.readBytes(dataByte);
				reverse(dataByte);
				if(logger.isDebugEnabled())
					logger.debug("cos$:{}",Public.byte2hex(dataByte));
				return Double.valueOf(Public.bytes2Int(dataByte))/1000;
				
			case DATATYPE_JDQTIAOZA:
				//bit15 ~ bit 10 无用
				dataByte = new byte[2];
				in.readBytes(dataByte);
				String state = Public.byte2hex(dataByte);
				state.substring(5, state.length());
				if(logger.isDebugEnabled())
					logger.debug("ZA:{}",state);
				return state;
				
			case DATATYPE_TEMPERATURE:
				dataByte = new byte[2];
				in.readBytes(dataByte);
				
				reverse(dataByte);
				if(logger.isDebugEnabled())
					logger.debug("TEMPERATURE:{}",Public.byte2hex(dataByte));
				return Double.valueOf(Public.bytes2Int(dataByte))/10;
				
			case DATATYPE_JDQSTATE:
				dataByte = new byte[2];
				in.readBytes(dataByte);
				reverse(dataByte);
				if(logger.isDebugEnabled())
					logger.debug("JDQSTATE:{}",Public.byte2hex(dataByte));
				return dataByte[1] == 0x01;
				
			default:
				return null;
		}
	}
	public static String byteToBit(byte b) {  
        return ""  
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)  
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)  
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)  
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);  
    } 
	public static void reverse(byte[] bs){
		byte tmp ;
		for(int i=0;i<bs.length/2;i++){
			tmp = bs[i];
			bs[i] = bs[bs.length - 1 -i];
			bs[bs.length - 1 - i] = tmp;
		}
	}

}
