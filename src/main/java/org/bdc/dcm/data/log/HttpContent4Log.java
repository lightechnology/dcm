package org.bdc.dcm.data.log;

import java.io.UnsupportedEncodingException;

import org.bdc.dcm.conf.ComConf;
import org.bdc.dcm.data.log.intf.Coder4Log;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataPackType;
import org.slf4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;

public class HttpContent4Log implements Coder4Log<HttpContent> {

	private String tab;
	private Logger logger;

	public HttpContent4Log(String tab, Logger logger) {
		this.tab = tab;
		this.logger = logger;
	}

	@Override
	public String log(HttpContent msg, DataPack dataPack) {
		if (DataPackType.HeartBeat == dataPack.getDataPackType())
			return null;
		ByteBuf byteBuf = msg.content();
		byteBuf.markReaderIndex();
		byte[] data = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(data);
		byteBuf.resetReaderIndex();
		String dataStr = null;
		try {
			dataStr = new String(data, ComConf.getInstance().CHARSET);
			if (logger.isInfoEnabled()) {
	            logger.info(tab, dataPack.getSocketAddress(), dataStr, dataPack.toString());
			}
			// 这里缓存数据
			// TODO Auto-generated method stub
		} catch (UnsupportedEncodingException e) {
			if (logger.isErrorEnabled()) {
			    logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return dataStr;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

}
