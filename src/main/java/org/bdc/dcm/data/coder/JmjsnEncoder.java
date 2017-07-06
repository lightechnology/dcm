package org.bdc.dcm.data.coder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
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

import org.bdc.dcm.conf.ComConf;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.Server;
import org.bdc.dcm.vo.e.DataPackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.netty.channel.ChannelHandlerContext;

public class JmjsnEncoder implements DataEncoder<String> {

	final static Logger logger = LoggerFactory.getLogger(JmjsnEncoder.class);

	private NettyBoot nettyBoot;

	public JmjsnEncoder(NettyBoot nettyBoot) {
		this.nettyBoot = nettyBoot;
	}

	@Override
	public String package2Data(ChannelHandlerContext ctx, DataPack msg) {
		StringBuilder paramterStr = new StringBuilder();
		paramterStr.append("tk=").append(System.currentTimeMillis());
		if (DataPackType.HeartBeat != msg.getDataPackType()) {
			Server server = nettyBoot.getServer();
			String jsonStr = dataPack2JsonStr(msg);
			if (null == jsonStr)
				return null;
			paramterStr.append("&json=").append(jsonStr);
			makeParamterStr(server.getSdkUserInfo(), paramterStr, jsonStr);
		}
		return paramterStr.toString();
	}

	protected Map<String, String> getNameAndValue(Object value) {
		@SuppressWarnings("unchecked")
		List<Object> v = (List<Object>) value;
		if (v.isEmpty() || 1 > v.size())
			return null;
		Map<String, String> result = new HashMap<String, String>();
		result.put("name", Public.objToStr(v.get(0)));
		result.put("value", Public.objToStr(v.get(1)));
		return result;
	}
	
	protected String cStr(String str, String ktr) {
		String ret = str;
		if (!"0".equals(ktr)) {
			if (!"".equals(ret))
				ret += "_";
			ret = ret + ktr;
		}
		return ret;
	}
	
	protected String cName(String name, String lb, String qb, String kb) {
		String str = "";
		str = cStr(str, lb);
		str = cStr(str, qb);
		str = cStr(str, kb);
		if (!"".equals(str))
			return str + "_" + name;
		return name;
	}
	
	protected Map<String, Object> getDmap(Entry<String, Object> entry) {
		String key = entry.getKey();
		if ("kind".equals(key)) return null;
		Map<String, String> mn = getNameAndValue(entry.getValue());
		String name = mn.get("name");
		String value = mn.get("value");
		String lb = key.substring(0, key.indexOf("_"));
		String kb = key.substring(key.lastIndexOf("_") + 1);
		String qb = "0";
		if (name.contains("_")) {
			qb = name.substring(0, name.indexOf("_"));
			name = name.substring(name.indexOf("_") + 1);
		}
		name = cName(name, lb, qb, kb);
		Map<String, Object> dmap = new HashMap<String, Object>();
		dmap.put("name", name);
		dmap.put("value", value);
		return dmap;
	}
	
	protected String dataPack2JsonStr(DataPack msg) {
		try {
			Template jsonftl = ComConf.getInstance().getFmkTemp("json1.ftl");
			StringWriter stringWriter = new StringWriter();
			BufferedWriter writer = new BufferedWriter(stringWriter);
			Map<String, Object> dataModel = new HashMap<String, Object>();
			dataModel.put("mac", msg.getMac());
			int kind = Public.objToInt(msg.getData().get("kind"));
			dataModel.put("kind", 0 < kind ? kind : 1);
			List<Map<String, Object>> infolist = new ArrayList<Map<String, Object>>();
			dataModel.put("infolist", infolist);
			for (Iterator<Entry<String, Object>> ite = msg.getData().entrySet().iterator(); ite.hasNext();) {
				Entry<String, Object> entry = ite.next();
				Map<String, Object> dmap = getDmap(entry);
				if (null != dmap)
					infolist.add(getDmap(entry));
			}
			jsonftl.process(dataModel, writer);
			String jsonStr = Public.trim(new String(stringWriter.toString()));
			writer.flush();
			writer.close();
			return jsonStr;
		} catch (TemplateException | IOException e) {
			if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return null;
	}
	
	protected void makeParamterStr(Map<String, String> sdkInfo, StringBuilder paramterStr, String jsonStr) {
		if (null != sdkInfo) {
			Iterator<Entry<String, String>> ite = sdkInfo.entrySet().iterator();
			if (ite.hasNext()) {
				Entry<String, String> entry = ite.next();
				String key = entry.getValue();
				String datetime = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
				datetime = datetime.substring(0, datetime.length() - 1) + "0";
				try {
					String sv = jsonStr + key + datetime;
					String verifyCode = Public.stringToMD5(sv);
					paramterStr.append("&token=").append(entry.getKey());
					paramterStr.append("&verifyCode=").append(verifyCode);
					if (logger.isInfoEnabled()) {
					    logger.info("sv: {} verifyCode: {}", sv, verifyCode);
					}
				} catch (NoSuchAlgorithmException
						| UnsupportedEncodingException e) {
					if (logger.isErrorEnabled()) {
		                logger.error(e.getMessage(), e);
					} else
						e.printStackTrace();
				}
			}
		}
	}
	
}
