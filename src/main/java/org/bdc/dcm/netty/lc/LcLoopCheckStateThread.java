package org.bdc.dcm.netty.lc;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class LcLoopCheckStateThread implements Runnable{
	
	private String mac;
	
	private boolean isRun;
	
	private ChannelHandlerContext ctx;
	
	public LcLoopCheckStateThread() {
		super();
		this.isRun = false;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public void run() {
		this.isRun = true;
		try {
			while(this.isRun){
				for(byte i=1;i<21 && this.isRun;i++){
					byte[] cmd = Public.hexString2bytes("FE A5 01 12 16 "+ mac +" 00 ");
					byte[] modbus = Public.hexString2bytes(Public.byte2hex_ex(i)+" 03 00 80 00 0F");
					byte[] crc16 = Public.crc16_A001(modbus);
					int sum=0;
					for(int j=2;j<cmd.length;j++){
						sum+=(cmd[j] & 0xff);
					}
					for(int j=0;j<modbus.length;j++){
						sum+=(modbus[j] & 0xff);
					}
					for(int j=0;j<crc16.length;j++){
						sum+=(crc16[j] & 0xff);
					}
					byte crcSumByte = ((byte) (sum & 0xff));
					ByteBuf bu = ctx.alloc().buffer();
					bu.writeBytes(cmd);
					bu.writeBytes(modbus);
					bu.writeByte(crc16[1]);
					bu.writeByte(crc16[0]);
					bu.writeByte(crcSumByte);
					//logger.info("轮训发送的命令：{} {} {} {} {}",Public.byte2hex(cmd),Public.byte2hex(modbus),Public.byte2hex_ex(crc16[1]),Public.byte2hex_ex(crc16[0]),Public.byte2hex_ex(crcSumByte));
					ctx.channel().writeAndFlush(bu);
					Thread.sleep(1000);
				}
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
