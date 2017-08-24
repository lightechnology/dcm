package org.bdc.dcm.data.convert.lcmdb;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.netty.ChannelHandlerContextDecorator;
import org.bdc.dcm.vo.Server;
import org.bdc.dcm.vo.e.DataType;
import org.bdc.dcm.vo.e.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class LcmdbLoopCheckStateThread implements Runnable{
	
	private Map<String,LcmdbLoopInfo>  loopInfoMap = new ConcurrentHashMap<>();
	
	private AtomicBoolean isRun = new AtomicBoolean(false);
	
	private final static Logger logger = LoggerFactory.getLogger(LcmdbLoopCheckStateThread.class);
	
	public LcmdbLoopCheckStateThread() {
		super();
	}

	public void addLoopInfo(LcmdbLoopInfo lcmdbLoopInfo){
		String channelId = lcmdbLoopInfo.getCtx().id();
		if(!loopInfoMap.containsKey(channelId))
			loopInfoMap.put(channelId, lcmdbLoopInfo);
	}
	public void removeLoopInfo(ChannelHandlerContextDecorator ctx){
		loopInfoMap.remove(ctx.id());
		if(loopInfoMap.isEmpty())
			isRun.set(false);
	}
	public boolean isRun() {
		return isRun.get();
	}

	public void setRun(boolean isRun) {
		this.isRun.set(isRun);
	}

	@Override
	public void run() {
		this.isRun.set(true);
		try {
			Optional<Server> optional = IntfConf.getServerConf().getServerConf().stream().filter(item->{return item.getDataType().equals(DataType.Lcmdb) && item.getServerType().equals(ServerType.TCP_SERVER);}).findFirst();
			if(!optional.isPresent()) { isRun.set(false);return;}
			Server server = optional.get();
			while(this.isRun.get()){
				List<LcmdbLoopInfo> lcmdbLoopInfos = loopInfoMap.values().stream().collect(Collectors.toList());
				for(byte i=1;i<21 && this.isRun.get();i++){
					for(int cNum = 0; cNum < lcmdbLoopInfos.size() && this.isRun.get(); cNum++){
						LcmdbLoopInfo info = lcmdbLoopInfos.get(cNum);
						String mac = info.getMac();
						ChannelHandlerContextDecorator ctx = info.getCtx();
						if(ctx.isRemoved()) break;
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
						ctx.writeAndFlush(bu);
					}
					Thread.sleep(server.getDelaySendingTime()*1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
