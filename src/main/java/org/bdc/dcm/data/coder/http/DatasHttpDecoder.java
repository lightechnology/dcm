package org.bdc.dcm.data.coder.http;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.util.List;
import java.util.stream.Collectors;

import org.bdc.dcm.conf.ComConf;
import org.bdc.dcm.data.coder.intf.DatasDecoder;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;

public class DatasHttpDecoder extends DataHttpDecoder implements DatasDecoder<HttpContent> {

	final static Logger logger = LoggerFactory.getLogger(DataHttpDecoder.class);

	private DatasDecoder<String> dataDecoder;

	public DatasHttpDecoder(DatasDecoder<String> dataDecoder) {
		super(dataDecoder);
		this.dataDecoder = dataDecoder;
	}
	
	@Override
	public List<DataPack> datas2Package(ChannelHandlerContext ctx, HttpContent msg) {
		ByteBuf byteBuf = msg.content();
		byteBuf.markReaderIndex();
		byte[] data = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(data);
		byteBuf.resetReaderIndex();
		try {
			SocketAddress socketAddress = ctx.channel().remoteAddress();
			List<DataPack> dataPacks = dataDecoder.datas2Package(ctx, new String(data, ComConf.getInstance().CHARSET));
			dataPacks = dataPacks.stream().map(item->{
				item.setSocketAddress(socketAddress);
				return item;
			}).collect(Collectors.toList());
			return dataPacks;
		} catch (UnsupportedEncodingException e) {
			if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return null;
	}
}
