package org.bdc.dcm.data.convert.ygdqmdb;

import java.util.Optional;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.convert.lcmdb.LcmdbLoopCheckStateThread;
import org.bdc.dcm.vo.Server;
import org.bdc.dcm.vo.e.DataType;
import org.bdc.dcm.vo.e.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class YgdqmdbCheckStateThread implements Runnable {

	private String mac;
	
	private boolean isRun;
	
	private ChannelHandlerContext ctx;
	
	private final static Logger logger = LoggerFactory.getLogger(LcmdbLoopCheckStateThread.class);
	
	public YgdqmdbCheckStateThread() {
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
			Optional<Server> optional = IntfConf.getServerConf().getServerConf().stream().filter(item->{return item.getDataType().equals(DataType.Ygdqmdb) && item.getServerType().equals(ServerType.TCP_SERVER);}).findFirst();
			if(!optional.isPresent()) { isRun = false;return;}
			Server server = optional.get();
			while(this.isRun){
				for(byte i=1;i<21 && this.isRun;i++){
					
					//modbus-------------------------------------------------------------
					byte[] modbus = Public.hexString2bytes(Public.byte2hex_ex(i)+" 03 00 00 00 4E");
					byte[] crc16 = Public.crc16_A001(modbus);
					
					ByteBuf bu = ctx.alloc().buffer();
					
					bu.writeBytes(modbus);
					bu.writeByte(crc16[1]);
					bu.writeByte(crc16[0]);
					ctx.channel().writeAndFlush(bu);
					
					Thread.sleep(server.getDelaySendingTime()*1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
