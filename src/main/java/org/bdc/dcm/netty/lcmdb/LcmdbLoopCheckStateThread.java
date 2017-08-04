package org.bdc.dcm.netty.lcmdb;

import java.util.Optional;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.vo.Server;
import org.bdc.dcm.vo.e.DataType;
import org.bdc.dcm.vo.e.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class LcmdbLoopCheckStateThread implements Runnable{
	
	private String mac;
	
	private boolean isRun;
	
	private ChannelHandlerContext ctx;
	
	private final static Logger logger = LoggerFactory.getLogger(LcmdbLoopCheckStateThread.class);
	
	public LcmdbLoopCheckStateThread() {
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
			Optional<Server> optional = IntfConf.getServerConf().getServerConf().stream().filter(item->{return item.getDataType().equals(DataType.Lcmdb) && item.getServerType().equals(ServerType.TCP_SERVER);}).findFirst();
			if(!optional.isPresent()) { isRun = false;return;}
			Server server = optional.get();
			while(this.isRun){
				for(byte i=1;i<21 && this.isRun;i++){
					//头部----------------------------------------------------------------
					int sum=0;
					byte[] cmd = Public.hexString2bytes("FE A5 01 12 16 "+ mac +" 00 ");
					for(int j=2;j<cmd.length;j++){
						sum+=(cmd[j] & 0xff);
					}
					//modbus-------------------------------------------------------------
					byte[] modbus = Public.hexString2bytes(Public.byte2hex_ex(i)+" 03 00 80 00 0F");
					byte[] crc16 = Public.crc16_A001(modbus);
					
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
					//检验和----------------------------------------------------------------
					bu.writeByte(crcSumByte);
					ctx.channel().writeAndFlush(bu);
					Thread.sleep(server.getDelaySendingTime()*1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
