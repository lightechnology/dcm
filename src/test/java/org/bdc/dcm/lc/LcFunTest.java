package org.bdc.dcm.lc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.channel.ChannelHandlerContext;

public class LcFunTest {

	private static Logger logger = LoggerFactory.getLogger(LcFunTest.class);
	
	private static boolean runFlag = false;
	
	private static void temperature(ChannelHandlerContext ctx, DataPack msg,int val){
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
			data.put("10", list);
			msg.setData(data);
			ctx.writeAndFlush(msg);
		}
	}
	private static void state(ChannelHandlerContext ctx, DataPack msg,boolean val){
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
			data.put("11", list);
			msg.setData(data);
			ctx.writeAndFlush(msg);
		}
	}
	private static void unlock(ChannelHandlerContext ctx, DataPack msg,boolean val){
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
			data.put("10", list);
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
					state(ctx,msg,false);
					Thread.sleep(10000);
					state(ctx,msg,true);
					Thread.sleep(10000);
					unlock(ctx,msg,false);
					Thread.sleep(10000);
					unlock(ctx,msg,true);
					Thread.sleep(10000);
					temperature(ctx,msg,0);
					Thread.sleep(10000);
					temperature(ctx,msg,22);
					Thread.sleep(10000);
					temperature(ctx,msg,23);
					Thread.sleep(10000);
					temperature(ctx,msg,24);
					Thread.sleep(10000);
					temperature(ctx,msg,25);
					Thread.sleep(10000);
					temperature(ctx,msg,26);
					Thread.sleep(10000);
					temperature(ctx,msg,27);
					Thread.sleep(10000);
					temperature(ctx,msg,28);
					Thread.sleep(10000);
					temperature(ctx,msg,29);
					Thread.sleep(10000);
					temperature(ctx,msg,30);
					Thread.sleep(10000);
					temperature(ctx,msg,0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
}
