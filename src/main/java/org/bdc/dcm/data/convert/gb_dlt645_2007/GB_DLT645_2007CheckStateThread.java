package org.bdc.dcm.data.convert.gb_dlt645_2007;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.utils.CommUtils;
import org.bdc.dcm.netty.ChannelHandlerContextDecorator;
import org.bdc.dcm.vo.Server;
import org.bdc.dcm.vo.e.DataType;
import org.bdc.dcm.vo.e.ServerType;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class GB_DLT645_2007CheckStateThread implements Runnable{
	
	private Map<String,GB_DLT645_2007LoopInfo> loopInfoMap = new ConcurrentHashMap<>();
	
	private AtomicBoolean isRun = new AtomicBoolean(false);
	
	private static final GB_DLT645_2007TypeConvert convert = GB_DLT645_2007TypeConvert.getConvert();
	
	private final static Logger logger = LoggerFactory.getLogger(GB_DLT645_2007CheckStateThread.class);
	
	public static JSONObject properties;
	
	static{
		loadProperties();
	}

	public static void loadProperties() {
		InputStream in = null;
		try {
			String filePath = "org/bdc/dcm/data/convert/gb_dlt645_2007/loopCmd.json";
			in = GB_DLT645_2007CheckStateThread.class.getClassLoader().getResourceAsStream(filePath);
			properties = Public.str2Json(CommUtils.getJsonStr(in));
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
	/**
	 *
	 * @param loopInfo 
	 * @throws IllegalAccessException 
	 */
	public void addLoopInfo(GB_DLT645_2007LoopInfo loopInfo){
		try {
			String channelId = loopInfo.getCtx().id();
			if(!loopInfoMap.containsKey(channelId)){
				loopInfo.bufs = gernatorBuf(loopInfo.getMac(), loopInfo.getCtx());
				loopInfoMap.put(channelId, loopInfo);
			}
			isRun.set(true);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
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
			Optional<Server> optional = IntfConf.getServerConf().getServerConf().stream().filter(item->{return item.getDataType().equals(DataType.Gb_dlt645_2007) && item.getServerType().equals(ServerType.TCP_SERVER);}).findFirst();
			if(!optional.isPresent()) { isRun.set(false);return;}
			Server server = optional.get();
			while(this.isRun.get()){
				
				Optional<Integer> o = loopInfoMap.values().stream().map(item->item.bufs.size()).max(new Comparator<Integer>() {

					@Override
					public int compare(Integer o1, Integer o2) {
						return o1 - o2;
					}
				});
				if(o.isPresent()){
					int max = o.get();
					for(int sendInfoIndex=0;sendInfoIndex<max;sendInfoIndex++){
						//防止遍历时 删除报错
						List<String> allCtxs = loopInfoMap.keySet().stream().collect(Collectors.toList());
						int channelMaxIndex = allCtxs.size();
						for(int channelIndex=0;channelIndex<channelMaxIndex;channelIndex++){
							//每个通道
							GB_DLT645_2007LoopInfo channelInfo = loopInfoMap.get(allCtxs.get(channelIndex));
							ChannelHandlerContextDecorator ctx = channelInfo.getCtx();
							List<byte[]> bufs = channelInfo.bufs;
							if(ctx.isRemoved()){
								loopInfoMap.remove(ctx);
								break;
							}
							if(sendInfoIndex < bufs.size()){
								byte[] bs = bufs.get(sendInfoIndex);
								ctx.writeAndFlush(ctx.alloc().buffer(bs.length).writeBytes(bs));
								Thread.sleep(1000);//不同通道同时写，写完统一休息
							}
						}
					}
				}
				Thread.sleep(server.getDelaySendingTime()*1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 依据配置 得到如果写轮询的规则
	 * @param addr 网关地址
	 * @param ctx
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static List<byte[]> gernatorBuf(String addr, ChannelHandlerContextDecorator ctx) throws IllegalAccessException {
		List<byte[]> bufs = new ArrayList<>();
		Map<String,List<String>> gbMap = (Map<String, List<String>>) properties.get(addr.replace(" ", "").toLowerCase());
		Set<String> gbKeys = gbMap.keySet();
		Iterator<String> iterator = gbKeys.iterator();
		//网关所有国标表
		while(iterator.hasNext()){
			String gbkey = iterator.next();
			//拿到所有需要在单个表多个发送的命令
			List<String> list = gbMap.get(gbkey);
			int max = list.size();
			for(int i=0;i<max;i++){
				String cmd = list.get(i);
				
				byte[] gbAddr = Public.hexString2bytes(gbkey);
				CommUtils.reverse(gbAddr);
				byte[] bs = convert.encoder(cmd, null,gbAddr);
				bufs.add(bs);
			}
		}
		return bufs;
	}
}