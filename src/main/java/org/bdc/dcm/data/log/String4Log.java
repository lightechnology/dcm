package org.bdc.dcm.data.log;

import org.bdc.dcm.data.log.intf.Coder4Log;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataPackType;
import org.slf4j.Logger;

public class String4Log implements Coder4Log<String> {
	
	private String tab;
	private Logger logger;
	
	public String4Log(String tab, Logger logger) {
		this.tab = tab;
		this.logger = logger;
	}

	@Override
	public String log(String msg, DataPack dataPack) {
		if (DataPackType.HeartBeat == dataPack.getDataPackType())
			return null;
		if (logger.isInfoEnabled()) {
            logger.info(tab, dataPack.getSocketAddress(), msg, dataPack.toString());
		}
		// 这里缓存数据
		return msg;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

}
