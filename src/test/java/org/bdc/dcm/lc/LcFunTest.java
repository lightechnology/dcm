package org.bdc.dcm.lc;

import static org.bdc.dcm.netty.lcmdb.LcmdbTypeConvert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.netty.lcmdb.LcmdbTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.channel.ChannelHandlerContext;

public class LcFunTest {

	private static Logger logger = LoggerFactory.getLogger(LcFunTest.class);
	
	private static boolean runFlag = false;
	
	
	/**
	 * 	@param ctx
	 * 	@param msg
	 * 	@param val
	 * 	@param order 10制冷,11继电器状态,12继电器控制,13 制暖,14剩余电量,15剩余用电时间
	 * 	</br>
	 *	<table>
	 *		<tr>JDQ_control</tr>
	 * 		<tr>
	 * 			<td>bit15</td><td>bit14</td><td>bit13</td><td>bit12</td><td>bit11</td><td>bit10</td><td>bit9</td><td>bit8</td><td>bit7</td><td>bit6</td><td>bit5</td><td>bit4</td><td>bit3</td><td>bit2</td><td>bit1</td><td>bit0</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>U_u</td><td>U_d</td><td>I_u</td><td>I_d</td><td>P_u</td><td>P_d</td><td>MS</td><td>locked</td><td>T_u</td><td>T_d</td><td>AA</td><td>BB</td><td>CC</td><td>DD</td><td>EE</td><td>FF</td>
	 * 		</tr>	
	 * 		<tr>
	 * 			<td>电压上限</td><td>电压下限</td><td>电流上限</td><td>电流下限</td><td>功率上限</td><td>功率上限</td><td>为1表示时段控制（自动）模式，为0表示非时段控制（手动）模式</td><td>表示远程锁定。1为锁定  0为不锁定</td><td>温度上限</td><td>温度下限</td><td>剩余电量使能选择位</td><td>剩余用电时间使能选择位</td><td>剩余电量写方式，1：累加写；0：清零写</td><td>剩余用电时间写方式，1：累加写；0：清零写</td><td>当前电能显示选择位</td><td>当前用电时间显示选择位</td>
	 * 		</tr>
	 *	</table>
	 * 
	 */
	private static void commCtr(ChannelHandlerContext ctx, DataPack msg,Object val,int type){
		String mac = msg.getMac();
		byte[] macBytes = Public.hexString2bytes(mac);
		logger.info("messageReceived:{}",mac);
		if(macBytes.length == 9 && macBytes[8] == (byte) 0x01){
			DataPack pack = new DataPack();
			pack.setMac("00 12 4B 00 0A DC 89 5B 01");
			pack.setSocketAddress(msg.getSocketAddress());
			Map<String, Object> data = new HashMap<>();
			List<Object> list = new ArrayList<>();
			list.add("");
			list.add(val);
			data.put(type+"", list);
			msg.setData(data);
			ctx.writeAndFlush(msg);
		}
	}
	
	public static void go(ChannelHandlerContext ctx, DataPack msg) throws Exception{
		if(runFlag) return ;
		runFlag = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
//					commCtr(ctx,msg,new byte[]{(byte)0xff,(byte)0xff},JDQ_Control);
//					Thread.sleep(10000);
//					commCtr(ctx,msg,3,DATATYPE_SURPLUS_POWER);
//					Thread.sleep(10000);
//					commCtr(ctx,msg,3,DATATYPE_SURPLUS_TIME);
//					
//					
//					Thread.sleep(10000);
//					
//					commCtr(ctx,msg,false,DATATYPE_JDQSTATE);
//					Thread.sleep(10000);
//					commCtr(ctx,msg,true,DATATYPE_JDQSTATE);
//					Thread.sleep(10000);
//					commCtr(ctx,msg,new byte[]{0,0},JDQ_Control);
//					Thread.sleep(10000);
//					commCtr(ctx,msg,new byte[]{1,0},JDQ_Control);
//					Thread.sleep(10000);
					//--------------制冷----------------------
					commCtr(ctx,msg,0,DATATYPE_TEMPERATURE_COLD);
					Thread.sleep(10000);
					commCtr(ctx,msg,22,DATATYPE_TEMPERATURE_COLD);
					Thread.sleep(10000);
					commCtr(ctx,msg,23,DATATYPE_TEMPERATURE_COLD);
					Thread.sleep(10000);
					commCtr(ctx,msg,24,DATATYPE_TEMPERATURE_COLD);
					Thread.sleep(10000);
					commCtr(ctx,msg,25,DATATYPE_TEMPERATURE_COLD);
					Thread.sleep(10000);
					commCtr(ctx,msg,26,DATATYPE_TEMPERATURE_COLD);
					Thread.sleep(10000);
					commCtr(ctx,msg,27,DATATYPE_TEMPERATURE_COLD);
					Thread.sleep(10000);
					commCtr(ctx,msg,28,DATATYPE_TEMPERATURE_COLD);
					Thread.sleep(10000);
					commCtr(ctx,msg,29,DATATYPE_TEMPERATURE_COLD);
					Thread.sleep(10000);
					commCtr(ctx,msg,30,DATATYPE_TEMPERATURE_COLD);
					Thread.sleep(10000);
					commCtr(ctx,msg,0,DATATYPE_TEMPERATURE_COLD);
					//--------------制热----------------------
					commCtr(ctx,msg,16,DATATYPE_TEMPERATURE_WARM);
					Thread.sleep(10000);
					commCtr(ctx,msg,17,DATATYPE_TEMPERATURE_WARM);
					Thread.sleep(10000);
					commCtr(ctx,msg,18,DATATYPE_TEMPERATURE_WARM);
					Thread.sleep(10000);
					commCtr(ctx,msg,19,DATATYPE_TEMPERATURE_WARM);
					Thread.sleep(10000);
					commCtr(ctx,msg,20,DATATYPE_TEMPERATURE_WARM);
					Thread.sleep(10000);
					commCtr(ctx,msg,21,DATATYPE_TEMPERATURE_WARM);
					Thread.sleep(10000);
					commCtr(ctx,msg,22,DATATYPE_TEMPERATURE_WARM);
					Thread.sleep(10000);
					commCtr(ctx,msg,23,DATATYPE_TEMPERATURE_WARM);
					Thread.sleep(10000);
					commCtr(ctx,msg,24,DATATYPE_TEMPERATURE_WARM);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
}
