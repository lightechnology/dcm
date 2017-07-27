package org.bdc.dcm.netty.lcmdb;

import org.bdc.dcm.data.coder.intf.TypeConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;


public class LcmdbTypeConvert implements TypeConvert{

	private static Logger logger = LoggerFactory.getLogger(LcmdbTypeConvert.class);

	private final static LcmdbTypeConvert convert = new LcmdbTypeConvert();
	
	
	
	public final static int DATATYPE_NOFOUND = -999;
	//----------------读---------------------------------
	public final static int DATATYPE_REMAININGTIMELONG = 128;
	public final static int DATATYPE_REMAININGELECTRICITY = 130;
	public final static int DATATYPE_TOTALTIME = 132;
	public final static int DATATYPE_TOTALACTIVEPOWER = 134;
	public final static int DATATYPE_U = 135;
	public final static int DATATYPE_I = 136;
	public final static int DATATYPE_P = 137;
	public final static int DATATYPE_COS$ = 138;
	public final static int DATATYPE_JDQTIAOZA = 139;
	public final static int DATATYPE_TEMPERATURE = 140;
	public final static int DATATYPE_JDQSTATE = 141;
	public final static int JDQ_Control = 142;
	//----------------写---------------------------------
	public final static int DATATYPE_TEMPERATURE_WARM = -1;
	public final static int DATATYPE_TEMPERATURE_COLD = -2;
	public final static int DATATYPE_SURPLUS_POWER= -3;
	public final static int DATATYPE_SURPLUS_TIME= -4;
	
	private LcmdbTypeConvert() {}
	
	public static LcmdbTypeConvert getConvert() {
		return convert;
	}



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
		if ("JDQ_control".equals(type)) {
			return JDQ_Control;
		}
		if("temperature_warm".equals(type)){
			return DATATYPE_TEMPERATURE_WARM;
		}
		if("temperature_cold".equals(type)){
			return DATATYPE_TEMPERATURE_COLD;
		}
		if("surplus_power".equals(type)){
			return DATATYPE_SURPLUS_POWER;
		}
		if("surplus_time".equals(type)){
			return DATATYPE_SURPLUS_TIME;
		}
		return DATATYPE_NOFOUND;
	}
	//不包括地址
	public static byte[] encoder(String type, Object val,byte modbusAddr){
		byte[] bs = null,tmp,crc;
		boolean isOpen;
		switch (convertTypeStr2TypeId(type)) {
			case DATATYPE_SURPLUS_POWER:
				return doubleByteCtr(val, modbusAddr,3200,(byte)0x22);
			case DATATYPE_SURPLUS_TIME:
				return doubleByteCtr(val, modbusAddr,10,(byte)0x29);
			case DATATYPE_TEMPERATURE_WARM:
				return temperature(val, modbusAddr,(byte)01);
			//温度 0 关机 22-30 则控制
			case DATATYPE_TEMPERATURE_COLD:
				return temperature(val, modbusAddr,(byte)03);
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
			case JDQ_Control:
				byte[] ctrCmd = (byte[]) val;
				bs = new byte[11];
				tmp = new byte[9];
				tmp[0] = modbusAddr;
				tmp[1] = (byte)0x10;//功能码
				tmp[2] = 00;
				tmp[3] = (byte)0x16;
				tmp[4] = 00;
				tmp[5] = 01;
				tmp[6] = 02;
				tmp[7] = ctrCmd[0];
				tmp[8] = (byte) (ctrCmd[1] & (byte)0xf3);//剩余电量和时间 都是清零设置
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
	/**
	 * 两字节寄存器 控制
	 * @param val
	 * @param modbusAddr
	 * @param 值偏移计算
	 * @param 寄存器基地址
	 * @return
	 */
	private static byte[] doubleByteCtr(Object val, byte modbusAddr,int offset,byte regBaseAddr) {
		byte[] bs;
		byte[] tmp;
		byte[] crc;
		int power = (Integer) val;
		bs = new byte[13];
		tmp = new byte[11];
		//01 10 00 22 00 02 04
		tmp[0] = modbusAddr;
		tmp[1] = (byte)0x10;
		tmp[2] = 0;
		tmp[3] = regBaseAddr;
		tmp[4] = 0;
		tmp[5] = 2;//寄存器长度
		tmp[6] = 4;//寄存器值长度
		byte[] bytes = Public.int2Bytes(power * offset, 4);
		reverse(bytes);
		tmp[7] = bytes[0];
		tmp[8] = bytes[1];
		tmp[9] = bytes[2];
		tmp[10] = bytes[3];
		crc = Public.crc16_A001(tmp);
		for(int i=0;i<tmp.length;i++)
			bs[i] = tmp[i];
		bs[11] = crc[1];
		bs[12] = crc[0];
		return bs;
	}
	
	
	/**
	 * 
	 * @param val
	 * @param modbusAddr
	 * @param flag 03制冷 01 制热
	 * @return
	 */
	private static byte[] temperature(Object val, byte modbusAddr,byte flag) {
		byte[] bs;
		byte[] tmp;
		byte[] crc;
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
			tmp[8] = flag;//制冷,制热标志位
		}
		crc = Public.crc16_A001(tmp);
		for(int i=0;i<tmp.length;i++)
			bs[i] = tmp[i];
		bs[9] = crc[1];
		bs[10] = crc[0];
		return bs;
	}
	public Object decode(String type, ByteBuf in) {
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
