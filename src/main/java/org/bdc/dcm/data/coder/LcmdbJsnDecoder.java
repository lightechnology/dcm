package org.bdc.dcm.data.coder;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DatasDecoder;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.Server;
import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.Datalevel;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.channel.ChannelHandlerContext;
import static org.bdc.dcm.netty.lc.LcTypeConvert.*;
public class LcmdbJsnDecoder implements DatasDecoder<String> {

	final static Logger logger = LoggerFactory.getLogger(LcmdbJsnDecoder.class);
	
	private NettyBoot nettyBoot;
	
	private final DataTabConf dataTabConf;
	
	public LcmdbJsnDecoder(NettyBoot nettyBoot) {
		this.nettyBoot = nettyBoot;
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, String msg) {
		try {
		Server server = nettyBoot.getServer();
		Map<String, String> sdkInfo = server.getSdkUserInfo();
		Map<String, Object> paramterMap = Public.getParamsFromURL(msg);
		String jsonStr = Public.objToStr(msg);
		String token = Public.objToStr(paramterMap.get("token"));
		String verifyCode = Public.objToStr(paramterMap.get("verifyCode"));
		String key = "";
		if (!"".equals(token)) {
			if (null != sdkInfo) {
				Iterator<Entry<String, String>> ite = sdkInfo.entrySet().iterator();
				while (ite.hasNext()) {
					Entry<String, String> entry = ite.next();
					if (token.equals(entry.getKey())) {
						key = entry.getValue();
						break;
					}
				}
			}
		}
		if ("".equals(key)) return null;
		if (!verifyParamter(jsonStr, verifyCode, key)) return null;
		
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return null;
	}
	protected boolean verifyParamter(String jsonStr, String verifyCode,
			String key) {
		Date now = new Date();
		for (int i = -1; i < 2; i++) {
			String[] rs = md5Paramter(jsonStr, key, new Date(now.getTime() + i
					* 60000));
			if (logger.isInfoEnabled()) {
			    logger.info("sv: {} verifyCode: {} mv: {}", jsonStr + key + rs[0], verifyCode, rs[1]);
			}
			if (verifyCode.equals(rs[1]))
				return true;
		}
		return false;
	}
	protected String[] md5Paramter(String jsonStr, String key, Date date) {
		String datetime = new SimpleDateFormat("yyyyMMddHHmm").format(date);
		datetime = datetime.substring(0, datetime.length() - 1) + "0";
		try {
			return new String[] { datetime,
					Public.stringToMD5(jsonStr + key + datetime) };
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return null;
	}
	protected Map<Integer, DataTab> getDataTabConf(int id) {
		List<DataTab> dataTabs = dataTabConf.getDataTabConf("Lc");
		Map<Integer, DataTab> dataTabMap = new HashMap<Integer, DataTab>();
		for (int i = 0; i < dataTabs.size(); i++) {
			DataTab dataTab = dataTabs.get(i);
			if (id == dataTab.getId()) {
				dataTabMap.put(dataTab.getId(), dataTab);
			}
		}
		if (dataTabMap.isEmpty())
			return null;
		return dataTabMap;
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
				int powerStatus = DATATYPE_DEFAULTFAIL;
				if(json.containsKey("power_status")) {
					String status = json.get("power_status")+"";
					switch(status) {
						case "off":powerStatus = 0;
						case "on":powerStatus = 1;
					}
				}
				// 温度调节 带℃单位
				int temperature = DATATYPE_DEFAULTFAIL;
				if(json.containsKey("setting_temperature"))
					temperature = Integer.valueOf(json.get("setting_temperature")+"");
				// 模式 cool-制冷 warm-制暖
				int temperatureCtr = DATATYPE_DEFAULTFAIL;
				if(json.containsKey("setting_mode")) {
					String mode = json.get("setting_mode")+"";
					switch(mode) {
						case "cool":temperatureCtr = DATATYPE_TEMPERATURE_COLD;
						case "warm":temperatureCtr = DATATYPE_TEMPERATURE_WARM;
					}
				}
				if(logger.isDebugEnabled())
					logger.debug("mac:{},开关状态:{},温度调节:{},模式 cool:{}",mac,powerStatus,temperature,temperatureCtr);
				
				DataPack dataPack = new DataPack();
				dataPack.setToMac(mac);
				dataPack.setOnlineStatus(1);
				dataPack.setDatalevel(Datalevel.NORMAL);
				dataPack.setDataPackType(DataPackType.Cmd);
				
				Map<String, Object> data = new HashMap<>();
				List<Object> list = new ArrayList<>();
				boolean sendOk = false;
				if(powerStatus == 0) {
					list.add("");
					list.add(0);
					data.put(DATATYPE_TEMPERATURE_COLD+"", list);
					sendOk = true;
				}else if(temperatureCtr == DATATYPE_TEMPERATURE_COLD) {
					list.add("");
					list.add(temperature);
					data.put(DATATYPE_TEMPERATURE_COLD+"", list);
					sendOk = true;
				}else if(temperatureCtr == DATATYPE_TEMPERATURE_WARM) {
					list.add("");
					list.add(temperature);
					data.put(DATATYPE_TEMPERATURE_WARM+"", list);
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
