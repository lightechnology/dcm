package org.bdc.dcm.data.coder;

import static org.bdc.dcm.netty.lcmdb.LcmdbTypeConvert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.data.coder.intf.DatasDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.Server;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.channel.ChannelHandlerContext;
import static org.bdc.dcm.data.coder.utils.CommUtils.*;
public class LcmdbJsnDecoder implements DatasDecoder<String> {

	final static Logger logger = LoggerFactory.getLogger(LcmdbJsnDecoder.class);
	
	private NettyBoot nettyBoot;
	
	public LcmdbJsnDecoder(NettyBoot nettyBoot) {
		this.nettyBoot = nettyBoot;
	}
	
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, String msg) {
		throw new AbstractMethodError("请实现");
	}

	
	@Override
	@SuppressWarnings({"unchecked","unused"})
	public List<DataPack> datas2Package(ChannelHandlerContext ctx, String msg) {
		Server server = nettyBoot.getServer();
		String jsonStr = Public.objToStr(msg);
		List<DataPack> dataPackList = new ArrayList<>();
		try {
			JSONObject json = Public.str2Json(jsonStr);
			List<String> macList = (List<String>) json.get("mac");
			for(int i=0;i<macList.size();i++) {
				String mac = macList.get(i);
				// 开关状态 on-开 off-关
				int powerStatus = 0;
				if(json.containsKey("power_status")) {
					String status = json.get("power_status")+"";
					switch(status) {
						case "on":powerStatus = 1;break;
						default:powerStatus = 0;break;
					}
				}
				// 温度调节 带℃单位
				int temperature = DATATYPE_NOFOUND;
				if(json.containsKey("setting_temperature"))
					temperature = Integer.valueOf(json.get("setting_temperature")+"");
				// 模式 cool-制冷 warm-制暖
				int temperatureCtr = DATATYPE_NOFOUND;
				if(json.containsKey("setting_mode")) {
					String mode = json.get("setting_mode")+"";
					switch(mode) {
						case "warm":temperatureCtr = DATATYPE_TEMPERATURE_WARM;break;
						case "cool":
						default:temperatureCtr = DATATYPE_TEMPERATURE_COLD;break;
					}
				}
				if(logger.isDebugEnabled())
					logger.debug("mac:{},开关状态:{},温度调节:{},模式 cool:{}",mac,powerStatus,temperature,temperatureCtr);
				
				DataPack dataPack = getInitCmdDataPack(mac);
				
				Map<String, Object> data = new HashMap<>();
				List<Object> list = new ArrayList<>();
				boolean sendOk = false;
				//传错命令直接关闭
				if(powerStatus == 0) {
					data.put(DATATYPE_TEMPERATURE_COLD+"", makeMapValue("", 0));
					sendOk = true;
				}else if(temperatureCtr == DATATYPE_TEMPERATURE_COLD) {
					data.put(DATATYPE_TEMPERATURE_COLD+"", makeMapValue("",temperature));
					sendOk = true;
				}else if(temperatureCtr == DATATYPE_TEMPERATURE_WARM) {
					data.put(DATATYPE_TEMPERATURE_WARM+"", makeMapValue("",temperature));
					sendOk = true;
				}
				if(sendOk) {
					dataPack.setData(data);
					dataPackList.add(dataPack);
				}
			}
			return dataPackList;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return dataPackList;
	}
}
