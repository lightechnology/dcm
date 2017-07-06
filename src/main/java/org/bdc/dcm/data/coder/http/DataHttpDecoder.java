package org.bdc.dcm.data.coder.http;

import java.io.UnsupportedEncodingException;

import org.bdc.dcm.conf.ComConf;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;

public class DataHttpDecoder implements DataDecoder<HttpContent> {

	final static Logger logger = LoggerFactory.getLogger(DataHttpDecoder.class);

	private DataDecoder<String> dataDecoder;

	public DataHttpDecoder(DataDecoder<String> dataDecoder) {
		this.dataDecoder = dataDecoder;
	}

	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, HttpContent msg) {
		ByteBuf byteBuf = msg.content();
		byteBuf.markReaderIndex();
		byte[] data = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(data);
		byteBuf.resetReaderIndex();
		try {
			return dataDecoder.data2Package(ctx, new String(data, ComConf.getInstance().CHARSET));
		} catch (UnsupportedEncodingException e) {
			if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return null;
	}

}
