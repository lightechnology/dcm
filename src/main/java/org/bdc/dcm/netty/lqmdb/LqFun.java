package org.bdc.dcm.netty.lqmdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.vo.DataPack;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class LqFun {

	public static void sendInitPack(ChannelHandlerContext ctx,byte addr,String mac) throws Exception{
		Thread.sleep(50);
		byte[] crc16 = Public.crc16_A001(new byte[]{addr,03,00,03,00,12});
		//mac 
		ByteBuf bu = ctx.alloc().buffer(8);
		bu.writeByte(addr);
		bu.writeByte(03);
		bu.writeBytes(Public.hexString2bytes("00 03"));
		bu.writeBytes(Public.hexString2bytes("00 0C"));
		bu.writeByte(crc16[1]);
		bu.writeByte(crc16[0]);
		ctx.writeAndFlush(bu);
	}
	/**
	 * 控制状态
	 * @param ctx
	 * @param addr 01 , 02
	 * @param reg
	 * @param state
	 * @return 
	 * @throws Exception
	 */
	public static void ctrState(ChannelHandlerContext ctx,DataPack dataPack,String macAndAddr,int reg,boolean state) throws Exception{
		Thread.sleep(50);
		dataPack.setMac(macAndAddr);
		Map<String, Object> data = new HashMap<>();
		List<Object> vl = new ArrayList<Object>();
		vl.add("");
		vl.add(state);
		//偏移
		data.put(reg+2+"", vl);
		dataPack.setData(data);
		ctx.fireChannelActive().writeAndFlush(dataPack);
	}
}
