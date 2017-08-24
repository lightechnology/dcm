package org.bdc.dcm.data.convert.gb_dlt645_2007;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.convert.ConfigUtils;
import org.bdc.dcm.data.convert.lcmdb.LcmdbLoopInfo;
import org.bdc.dcm.netty.ChannelHandlerContextDecorator;
import org.bdc.dcm.vo.Server;
import org.bdc.dcm.vo.e.DataType;
import org.bdc.dcm.vo.e.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class GB_DLT645_2007CheckStateThread implements Runnable{
	
	private Map<String,LcmdbLoopInfo>  loopInfoMap = new ConcurrentHashMap<>();
	
	private AtomicBoolean isRun = new AtomicBoolean(false);
	
	private static final GB_DLT645_2007TypeConvert convert = GB_DLT645_2007TypeConvert.getConvert();
	
	private final static Logger logger = LoggerFactory.getLogger(GB_DLT645_2007CheckStateThread.class);
	
	public static Properties properties;
	
	static{
		loadProperties();
	}

	public static void loadProperties() {
		InputStream in = null;
		try {
			String filePath = "org/bdc/dcm/data/convert/gb_dlt645_2007/loopCmd.properties";
			Properties p = new Properties();
			in = GB_DLT645_2007CheckStateThread.class.getClassLoader().getResourceAsStream(filePath);
			p.load(in);
			Iterator<Object> keys = p.keySet().iterator();
			properties = new Properties();
			while(keys.hasNext()){
				Object key = keys.next();
				String reverseKey = ConfigUtils.reverse((String)key,2);
				properties.put(reverseKey, p.get(key));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public GB_DLT645_2007CheckStateThread() {
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
				List<LcmdbLoopInfo> threadLoopInfos = loopInfoMap.values().stream().collect(Collectors.toList());
				//遍历所有channel 网关	
				for(int cNum = 0; cNum < threadLoopInfos.size() && this.isRun.get(); cNum++){
					LcmdbLoopInfo info = threadLoopInfos.get(cNum);
					//电表地址
					String mac = info.getMac();
					ChannelHandlerContextDecorator ctx = info.getCtx();
					if(ctx.isRemoved()) break;
					
					String cmdStr = (String) properties.get(mac);
					String[] cmdArr = cmdStr.split(",");
					for(int i=0;i<cmdArr.length;i++){
						//拿到该类型电表需要的发送指定
						byte[] bs = convert.encoder(cmdArr[i], null, Public.hexString2bytes(mac));
						if(bs.length > 0)
							ctx.writeAndFlush(bs);
					}
					
					
				}
				Thread.sleep(server.getDelaySendingTime()*1000);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
