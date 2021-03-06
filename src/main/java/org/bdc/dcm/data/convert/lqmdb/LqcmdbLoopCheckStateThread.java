package org.bdc.dcm.data.convert.lqmdb;

import java.util.Optional;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.vo.Server;
import org.bdc.dcm.vo.e.DataType;
import org.bdc.dcm.vo.e.ServerType;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class LqcmdbLoopCheckStateThread implements Runnable {

	private boolean isRun;
	
	private ChannelHandlerContext ctx;
	
	public LqcmdbLoopCheckStateThread() {
		super();
		this.isRun = false;
	}
	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
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
			Optional<Server> optional = IntfConf.getServerConf().getServerConf().stream().filter(item->{return item.getDataType().equals(DataType.Lqmdb) && item.getServerType().equals(ServerType.TCP_SERVER);}).findFirst();
			if(!optional.isPresent()) { isRun = false;return;}
			Server server = optional.get();
			while(this.isRun){
				for(byte i=1;i<21 && this.isRun;i++){
					//modbus-------------------------------------------------------------
					byte[] modbus = Public.hexString2bytes(Public.byte2hex_ex(i)+" 03 00 03 00 0C B4 4B");
					byte[] crc16 = Public.crc16_A001(modbus);
					int sum=0;
					
					for(int j=0;j<modbus.length;j++){
						sum+=(modbus[j] & 0xff);
					}
					for(int j=0;j<crc16.length;j++){
						sum+=(crc16[j] & 0xff);
					}
					byte crcSumByte = ((byte) (sum & 0xff));
					ByteBuf bu = ctx.alloc().buffer();
					bu.writeBytes(modbus);
					bu.writeByte(crc16[1]);
					bu.writeByte(crc16[0]);
					bu.writeByte(crcSumByte);
					ctx.channel().writeAndFlush(bu);
					Thread.sleep(server.getDelaySendingTime()*1000);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
