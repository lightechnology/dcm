package org.bdc.dcm.data.log;

import org.bdc.dcm.data.log.intf.Coder4Log;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataPackType;
import org.slf4j.Logger;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class ByteBuf4Log implements Coder4Log<ByteBuf> {
	
	private String tab;
	private Logger logger;
	
	public ByteBuf4Log(String tab, Logger logger) {
		this.tab = tab;
		this.logger = logger;
	}

	@Override
	public String log(ByteBuf msg, DataPack dataPack) {
		if (DataPackType.HeartBeat == dataPack.getDataPackType())
			return null;
		msg.markReaderIndex();
		byte[] data = new byte[msg.readableBytes()];
		msg.readBytes(data);
		String dataStr = Public.byte2hex(data);
		msg.resetReaderIndex();
		if (logger.isInfoEnabled()) {
		    logger.info(tab, dataPack.getSocketAddress(), dataStr, dataPack.toString());
		}
		// 这里缓存数据
		return dataStr;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
	
}
