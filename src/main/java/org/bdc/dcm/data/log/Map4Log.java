package org.bdc.dcm.data.log;

import java.util.Map;

import org.bdc.dcm.data.log.intf.Coder4Log;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataPackType;
import org.slf4j.Logger;

import com.util.tools.Public;

public class Map4Log implements Coder4Log<Map<String, Object>> {
	
	private String tab;
	private Logger logger;
	
	public Map4Log(String tab, Logger logger) {
		this.tab = tab;
		this.logger = logger;
	}

	@Override
	public String log(Map<String, Object> msg, DataPack dataPack) {
		if (DataPackType.HeartBeat == dataPack.getDataPackType())
			return null;
		String dataStr;
		try {
			dataStr = Public.map2JsonStr(msg);
			if (logger.isInfoEnabled()) {
                logger.info(tab, dataPack.getSocketAddress(), dataStr, dataPack.toString());
			}
			// 这里缓存数据
			return dataStr;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return null;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

}
