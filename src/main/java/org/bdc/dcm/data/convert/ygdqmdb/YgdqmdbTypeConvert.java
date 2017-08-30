package org.bdc.dcm.data.convert.ygdqmdb;

import java.util.function.Function;

import org.bdc.dcm.data.coder.intf.TypeConvert;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import static org.bdc.dcm.data.coder.utils.CommUtils.*;

public class YgdqmdbTypeConvert implements TypeConvert {

	//----------------error----------------
	public final static int ERROR_INT_VALUE = Integer.MIN_VALUE;
	public final static String ERROR_STRING_VALUE = null;
	//-----------------2 byte----------------
	
	public final static int DATATYPE_XS1 = CommType.eleShow.ordinal();
	
	public final static int DATATYPE_DZ = CommType.addr.ordinal();
	
	public final static int DATATYPE_BUAD = CommType.buad.ordinal();
	
	public final static int DATATYPE_U_SCL = CommType.uScope.ordinal();
	
	public final static int DATATYPE_I_SCL = CommType.iScope.ordinal();
	
	public final static int DATATYPE_NET = CommType.net.ordinal();

	public final static int DATATYPE_PT = CommType.vTimes.ordinal();
	
	public final static int DATATYPE_CT = CommType.iTimes.ordinal();
	
	public final static int DATATYPE_STATUS = CommType.hold.ordinal();
	
	public final static int DATATYPE_UPPER_LIMIT_ALARAM = CommType.upperLimitAlaram.ordinal();
	
	public final static int DATATYPE_LOWER_LIMIT_ALARAM = CommType.lowerLimitAlaram.ordinal();
	
	public final static int DATATYPE_TRANSMISSION_MODE = CommType.transmissionMode.ordinal();
	
	public final static int DATATYPE_TRANSMIT_PARAM_RATIO = CommType.transmitParamRatio.ordinal();
	
	//-----------------4 byte----------------
	
	public final static int DATATYPE_U_a = CommType.U_A.ordinal();
	
	public final static int DATATYPE_U_b = CommType.U_B.ordinal();

	public final static int DATATYPE_U_c = CommType.U_C.ordinal();
	
	public final static int DATATYPE_I_a = CommType.I_A.ordinal();
	
	public final static int DATATYPE_I_b = CommType.I_B.ordinal();
	
	public final static int DATATYPE_I_c = CommType.I_C.ordinal();
	
	public final static int DATATYPE_E_abc = CommType.average_I_abc.ordinal();
	
	public final static int DATATYPE_PS = CommType.allActivePower.ordinal();
	
	public final static int DATATYPE_QS = CommType.allReactivPower.ordinal();
	
	public final static int DATATYPE_PFS = CommType.allPowerFactor.ordinal();
	
	public final static int DATATYPE_HZ = CommType.hz.ordinal();
	
	public final static int DATATYPE_EPP = CommType.forwardActiveElectricEnergy_1.ordinal();
	
	public final static int DATATYPE_EPN = CommType.negativeActiveElectricEnergy_1.ordinal();
	
	public final static int DATATYPE_EQP = CommType.forwardReactiveElectricEnergy_1.ordinal();
	
	public final static int DATATYPE_EQN = CommType.negativeReactiveElectricalEnergy_1.ordinal();
	
	public final static int DATATYPE_P_A = CommType.P_A.ordinal();
	
	public final static int DATATYPE_P_B = CommType.P_B.ordinal();
	
	public final static int DATATYPE_P_C = CommType.P_C.ordinal();
	
	public final static int DATATYPE_Q_A = CommType.Q_A.ordinal();
	
	public final static int DATATYPE_Q_B = CommType.Q_B.ordinal();
	
	public final static int DATATYPE_Q_C = CommType.Q_C.ordinal();
	
	public final static int DATATYPE_S_A = CommType.apparent_P_A.ordinal();
	
	public final static int DATATYPE_S_B = CommType.apparent_P_B.ordinal();
	
	public final static int DATATYPE_S_C = CommType.apparent_P_C.ordinal();
	
	public final static int DATATYPE_S_S = CommType.allApparent.ordinal();
	
	public final static int DATATYPE_PFA = CommType.powerFactor_A.ordinal();
	
	public final static int DATATYPE_PFB = CommType.powerFactor_B.ordinal();
	
	public final static int DATATYPE_PFC = CommType.powerFactor_C.ordinal();
	
	public final static int DATATYPE_U_ab = CommType.U_AB.ordinal();
	
	public final static int DATATYPE_U_bc = CommType.U_BC.ordinal();
	
	public final static int DATATYPE_U_ca = CommType.U_CA.ordinal();
	
	public final static int DATATYPE_WPP = CommType.forwardActiveElectricEnergy_2.ordinal();
	
	public final static int DATATYPE_WPN = CommType.negativeActiveElectricEnergy_2.ordinal();
	
	public final static int DATATYPE_WQP = CommType.forwardReactiveElectricEnergy_2.ordinal();
	
	public final static int DATATYPE_WQN = CommType.negativeReactiveElectricalEnergy_2.ordinal();
	
	public final static int DATATYPE_NOFOUND = COMMTYPE_NOFOUND;
	
	
	private final static YgdqmdbTypeConvert convert = new YgdqmdbTypeConvert();
	
	public static YgdqmdbTypeConvert getConvert() {
		return convert;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static int propertiesToSysOrder(String type) {
		
		switch(type){
			case "Xs1":return DATATYPE_XS1;
			case "DZ":return DATATYPE_DZ;
			case "BUAD":return DATATYPE_BUAD;
			case "U.SCL":return DATATYPE_U_SCL;
			case "I.scl":return DATATYPE_I_SCL;
			case "net":return DATATYPE_NET;
			case "PT":return DATATYPE_PT;
			case "CT":return DATATYPE_CT;
			case "STATUS":return DATATYPE_STATUS;
			case "Ua":return DATATYPE_U_a;
			case "Ub":return DATATYPE_U_b;
			case "Uc":return DATATYPE_U_c;
			case "Ia":return DATATYPE_I_a;
			case "Eabc":return DATATYPE_E_abc;
			case "PS":return DATATYPE_PS;
			case "QS":return DATATYPE_QS;
			case "PFS":return DATATYPE_PFS;
			case "HZ":return DATATYPE_HZ;
			case "EPP":return DATATYPE_EPP;
			case "EPN":return DATATYPE_EPN;
			case "EQP":return DATATYPE_EQP;
			case "EQN":return DATATYPE_EQN;
			case "PA":return DATATYPE_P_A;
			case "PB":return DATATYPE_P_B;
			case "PC":return DATATYPE_P_C;
			case "QA":return DATATYPE_Q_A;
			case "QB":return DATATYPE_Q_B;
			case "QC":return DATATYPE_Q_C;
			case "SA":return DATATYPE_S_A;
			case "SB":return DATATYPE_S_B;
			case "SC":return DATATYPE_S_C;
			case "SS":return DATATYPE_S_S;
			case "PFA":return DATATYPE_PFA;
			case "PFB":return DATATYPE_PFB;
			case "PFC":return DATATYPE_PFC;
			case "Uab":return DATATYPE_U_ab;
			case "Ubc":return DATATYPE_U_bc;
			case "Uca":return DATATYPE_U_ca;
			case "WPP":return DATATYPE_WPP;
			case "WPN":return DATATYPE_WPN;
			case "WQP":return DATATYPE_WQP;
			case "WQN":return DATATYPE_WQN;
			case "ULA":return DATATYPE_UPPER_LIMIT_ALARAM;
			case "LLA":return DATATYPE_LOWER_LIMIT_ALARAM;
			case "TM":return DATATYPE_TRANSMISSION_MODE;
			case "TPR":return DATATYPE_TRANSMIT_PARAM_RATIO;
			
			default:return DATATYPE_NOFOUND;
					
		}
		
		
	}
	private Object convertIn(ByteBuf in,int byteLen,Function<byte[],Object> fun){
		byte[] bs = new byte[byteLen];
		in.readBytes(bs);
		in.resetReaderIndex();
		return fun.apply(bs);
	}
	@Override
	public Object decode(String type, ByteBuf in) {
		in.markReaderIndex();
		
		int sysOrder = propertiesToSysOrder(type);
		
		if(sysOrder == DATATYPE_NOFOUND) 
			return ERROR_INT_VALUE;
		
		else if(sysOrder == DATATYPE_XS1) 
			return convertIn(in, 2, new Function<byte[], Object>() {
				
				@Override
				public Object apply(byte[] bs) {
					reverse(bs);
					int tmp = Public.bytes2Int(bs);
					return tmp >  -1 && tmp < 9  ? tmp : ERROR_INT_VALUE;
				}
			});
		
		else if(sysOrder == DATATYPE_DZ)
			return convertIn(in, 2, new Function<byte[], Object>() {
				
				@Override
				public Object apply(byte[] bs) {
					reverse(bs);
					int tmp = Public.bytes2Int(bs);
					return tmp > 0 && tmp < 248 ? tmp: ERROR_INT_VALUE ;
				}
			});
		
		else if(sysOrder == DATATYPE_BUAD)
			return convertIn(in, 2, new Function<byte[], Object>() {
				
				@Override
				public Object apply(byte[] bs) {
					reverse(bs);
					int tmp = Public.bytes2Int(bs);
					switch(tmp){
						case 0:return 9600;
						case 1:return 4800;
						case 2:return 2400;
						default: return -1;
					}
				}
			});
		
		else if(sysOrder == DATATYPE_U_SCL)
			return convertIn(in, 2, new Function<byte[], Object>() {
				
				@Override
				public Object apply(byte[] bs) {
					reverse(bs);
					int tmp = Public.bytes2Int(bs);
					switch(tmp){
						case 0:return 400;
						case 1:return 100;
						default: return -1;
					}
				}
			});
		
		else if(sysOrder == DATATYPE_I_SCL)
			return convertIn(in, 2, new Function<byte[], Object>() {
				
				@Override
				public Object apply(byte[] bs) {
					reverse(bs);
					int tmp = Public.bytes2Int(bs);
					switch(tmp){
						case 0:return 5;
						case 1:return 1;
						default: return ERROR_INT_VALUE;
					}
				}
			});
		
		else if(sysOrder == DATATYPE_NET)
			return convertIn(in, 2, new Function<byte[], Object>() {
				
				@Override
				public Object apply(byte[] bs) {
					reverse(bs);
					int tmp = Public.bytes2Int(bs);
					switch(tmp){
						case 0:return "N.3.3";
						case 1:return "N34";
						default: return ERROR_INT_VALUE;
					}
				}
			});
		
		else if(sysOrder == DATATYPE_PT)
			return convertIn(in, 2, new Function<byte[], Object>() {
				
				@Override
				public Object apply(byte[] bs) {
					reverse(bs);
					return Public.bytes2Int(bs);
				}
			});
		
		else if(sysOrder == DATATYPE_CT)
			return convertIn(in, 2, new Function<byte[], Object>() {
				
				@Override
				public Object apply(byte[] bs) {
					reverse(bs);
					return Public.bytes2Int(bs);
				}
			});
		
		else if(sysOrder == DATATYPE_STATUS)
			return convertIn(in, 2, new Function<byte[], Object>() {
				
				@Override
				public Object apply(byte[] bs) {
					return ERROR_INT_VALUE;
				}
			});
		
		else 
			return in.readFloat();
	}

	public static byte[] encoder(String type, Object object, byte modbusAddr) {
		
		int sysOrder = propertiesToSysOrder(type);
		if(sysOrder == DATATYPE_CT)
			return funCode10(object, modbusAddr, (byte)0, (byte)0,2);
		
		else if(sysOrder == DATATYPE_PT)
			return funCode10(object, modbusAddr, (byte)0, (byte)2, 2);
		
		else if(sysOrder == DATATYPE_DZ)
			return funCode10(object, modbusAddr, (byte)0, (byte)3, 2);
		
		else if(sysOrder == DATATYPE_XS1)
			return funCode10(object, modbusAddr, (byte)0, (byte)4, 2);
		
		else if(sysOrder == DATATYPE_UPPER_LIMIT_ALARAM)
			return funCode10(object, modbusAddr, (byte)0, (byte)5, 2);
		
		else if(sysOrder == DATATYPE_LOWER_LIMIT_ALARAM)
			return funCode10(object, modbusAddr, (byte)0, (byte)6, 2);
		
		else if(sysOrder == DATATYPE_TRANSMISSION_MODE)
			return funCode10(object, modbusAddr, (byte)0, (byte)7, 2);
		
		else if(sysOrder == DATATYPE_TRANSMIT_PARAM_RATIO)
			return funCode10(object, modbusAddr, (byte)0, (byte)8, 2);
		
		return null;
	}

	private static byte[] funCode10(Object object, byte modbusAddr, byte reAddrH, byte reAddrL,int iArrayLen) {
		byte[] result = null,tmp,crc,data;
		data = Public.int2Bytes((int)object, iArrayLen);
		tmp = new byte[]{modbusAddr,10,reAddrH,reAddrL,data[1],data[0]};
		crc = Public.crc16_A001(tmp);
		result = new byte[tmp.length+crc.length];
		for(int i=0;i<tmp.length;i++)
			result[i] = tmp[i];
		result[result.length-2] = crc[1];
		result[result.length-1] = crc[0];
		return result;
	}
}
