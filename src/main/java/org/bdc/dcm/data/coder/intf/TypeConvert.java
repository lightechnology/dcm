package org.bdc.dcm.data.coder.intf;

import io.netty.buffer.ByteBuf;

public interface TypeConvert {
	public enum CommType{
		/**
		 * 电量显示选择
		 */
		 eleShow,
		/**
		 * 仪表地址
		 */
		 addr,
		/**
		 * 波特率
		 */
		 buad,
		/**
		 * 电压范围
		 */
		 uScope,
		/**
		 * 电流范围
		 */
		 iScope,
		/**
		 * 网络类型
		 */
		 net,
		/**
		 * 电压倍率
		 */
		 vTimes,
		/**
		 * 电流倍率
		 */
		 iTimes,
		/**
		 * 状态(保留)
		 */
		 hold,
		/**
		 * A相电压
		 */
		 U_A,
		/**
		 * B相电压
		 */
		 U_B,
		/**
		 * C相电压
		 */
		 U_C,
		/**
		 * A相电流
		 */
		 I_A,
		/**
		 * B相电流
		 */
		 I_B,
		/**
		 * C相电流
		 */
		 I_C,
		/**
		 * 平均电流
		 */
		 average_I_abc,
		/**
		 * 总有功功率
		 */
		 allActivePower,
		/**
		 * 总无功功率
		 */
		 allReactivPower,
		/**
		 * 总功率因数
		 */
		 allPowerFactor,
		/**
		 * 频率
		 */
		 hz,
		/**
		 * 正向有功电能 一次侧
		 */
		 forwardActiveElectricEnergy_1,
		/**
		 * 负向有功电能 一次侧
		 */
		 negativeActiveElectricEnergy_1,
		/**
		 * 正向无功电能 一次侧
		 */
		 forwardReactiveElectricEnergy_1,
		/**
		 * 负向无功电能 一次侧
		 */
		 negativeReactiveElectricalEnergy_1,
		/**
		 * A相有功功率
		 */
		 P_A,
		/**
		 * B相有功功率
		 */
		 P_B,
		/**
		 * C相有功功率
		 */
		 P_C,
		/**
		 * A相无功功率
		 */
		 Q_A,
		/**
		 * B相无功功率
		 */
		 Q_B,
		/**
		 * C相无功功率
		 */
		 Q_C,
		/**
		 * A相视在功率
		 */
		 apparent_P_A,
		/**
		 * B相视在功率
		 */
		 apparent_P_B,
		/**
		 * C相视在功率
		 */
		 apparent_P_C,
		/**
		 * 总视在功率
		 */
		 allApparent,
		/**
		 * A相功率因数
		 */
		 powerFactor_A,
		/**
		 * B相功率因数
		 */
		 powerFactor_B,
		/**
		 * C相功率因数
		 */
		 powerFactor_C,
		/**
		 * AB线电压
		 */
		 U_AB,
		/**
		 * BC线电压
		 */
		 U_BC,
		/**
		 * CA线电压
		 */
		 U_CA,
		/**
		 * 正向有功电能 二次侧
		 */
		 forwardActiveElectricEnergy_2,
		/**
		 * 负向有功电能 二次侧
		 */
		 negativeActiveElectricEnergy_2,
		
		/**
		 * 正向无功电能 二次侧
		 */
		 forwardReactiveElectricEnergy_2,
		/**
		 * 负向无功电能 二次侧
		 */
		 negativeReactiveElectricalEnergy_2,
		 /**
		  * 上限报警值
		  */
		 upperLimitAlaram,
		 /**
		  * 下限报警值
		  */
		 lowerLimitAlaram,
		 /**
		  * 变送方式
		  */
		 transmissionMode,
		 /**
		  * 变送参数比
		  */
		 transmitParamRatio,
		/**
		 * 无法找到
		 */
		 NOFOUND
	}
	//----------------error----------------
	public final static int ERROR_INT_VALUE = Integer.MIN_VALUE;
	public final static String ERROR_STRING_VALUE = null;
	public final static int COMMTYPE_NOFOUND = -999;
		
	public Object decode(String type, ByteBuf in) ;
}
