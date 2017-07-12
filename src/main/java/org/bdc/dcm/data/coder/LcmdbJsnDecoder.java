package org.bdc.dcm.data.coder;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.Server;
import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.Datalevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.channel.ChannelHandlerContext;

public class LcmdbJsnDecoder implements DataDecoder<String> {

	final static Logger logger = LoggerFactory.getLogger(LcmdbJsnDecoder.class);
	
	private NettyBoot nettyBoot;
	
	private final DataTabConf dataTabConf;
	
	public LcmdbJsnDecoder(NettyBoot nettyBoot) {
		this.nettyBoot = nettyBoot;
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, String msg) {
		Server server = nettyBoot.getServer();
		Map<String, String> sdkInfo = server.getSdkUserInfo();
		Map<String, Object> paramterMap = Public.getParamsFromURL(msg);
		String jsonStr = Public.objToStr(paramterMap.get("json"));
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
		try {
			Map<String, Object> json = Public.str2Map(jsonStr);
			String mac = Public.objToStr(json.get("device"));
			int kind = Public.objToInt(json.get("kind"));
			if (!"".equals(mac) && 0 < kind) {
				//Map<Integer, DataTab> dataTabMap = getDataTabConf(kind);
				DataPack dataPack = new DataPack();
				dataPack.setMac(mac);
				dataPack.setOnlineStatus(1);
				dataPack.setDatalevel(Datalevel.NORMAL);
				dataPack.setDataPackType(DataPackType.Info);
				/*Iterator<Entry<String, Object>> ite = json.entrySet().iterator();
				while (ite.hasNext()) {
					Entry<String, Object> entry = ite.next();
				}*/
			}
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
}
