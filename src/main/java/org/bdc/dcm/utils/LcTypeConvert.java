package org.bdc.dcm.utils;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LcTypeConvert extends CommTypeConvert{

	
	private static Logger logger = LoggerFactory.getLogger(LcTypeConvert.class); 
	
	private final static int DATATYPE_TEMPERATURE = 10;
	private final static int DATATYPE_JDQSTATE = 11;
	private final static int DATATYPE_JDQ_Control = 12;
	private final static int DATATYPE_TEMPERATURE_WARM = 13;
	private final static int DATATYPE_SURPLUS_POWER= 14;
	private final static int DATATYPE_SURPLUS_TIME= 15;
	

	
	@Override
	protected void initTypeTokey() throws Exception{
		
		typeToBackConvert.put(128, d10);
		typeToBackConvert.put(130, d3200);
		typeToBackConvert.put(132, d10);
		typeToBackConvert.put(134, d3200);
		typeToBackConvert.put(136, d100);
		typeToBackConvert.put(137, d1000);
		typeToBackConvert.put(138, null);
		typeToBackConvert.put(139, d1000);
		typeToBackConvert.put(140, new Function<Number, Integer>() {

			@Override
			public Integer apply(Number i) {
				return i.intValue() & 0xff;
			}
			
		});
		typeToBackConvert.put(141, d10);
		//只有一路继电器
		typeToBackConvert.put(142, numberToBoolean);
	}
	
	public int convertTypeStr2TypeId(String type) {
	
		if ("JDQ_control".equals(type)) {
			return DATATYPE_JDQ_Control;
		}
		if("temperature_warm".equals(type)){
			return DATATYPE_TEMPERATURE_WARM;
		}
		if("surplus_power".equals(type)){
			return DATATYPE_SURPLUS_POWER;
		}
		if("surplus_time".equals(type)){
			return DATATYPE_SURPLUS_TIME;
		}
		return -1;
	}
	//不包括地址
	public byte[] outModbusBytesByType(String type, Object val,byte modbusAddr){
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
			case DATATYPE_TEMPERATURE:
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
			case DATATYPE_JDQ_Control:
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
	private byte[] doubleByteCtr(Object val, byte modbusAddr,int offset,byte regBaseAddr) {
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
	private byte[] temperature(Object val, byte modbusAddr,byte flag) {
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
	

}
