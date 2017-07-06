package org.bdc.dcm.data.coder.http;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.data.coder.intf.HttpMessageFactory;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataPackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;

public class DataHttpEncoder implements DataEncoder<HttpContent> {

	final static Logger logger = LoggerFactory.getLogger(DataHttpEncoder.class);

	private NettyBoot nettyBoot;
	private HttpMessageFactory httpMessageFactory;
	private DataEncoder<String> dataEncoder;

	public DataHttpEncoder(HttpMessageFactory httpMessageFactory,
			NettyBoot nettyBoot, DataEncoder<String> dataEncoder) {
		this.httpMessageFactory = httpMessageFactory;
		this.nettyBoot = nettyBoot;
		this.dataEncoder = dataEncoder;
	}

	@Override
	public HttpContent package2Data(ChannelHandlerContext ctx, DataPack msg) {
		InetSocketAddress remoteAddress = (InetSocketAddress) msg
				.getSocketAddress();
		try {
			// DataPack转成想要的数据string
			String cast = dataEncoder.package2Data(ctx, msg);
			String path = nettyBoot.getServer().getPath();
			if (DataPackType.HeartBeat == msg.getDataPackType())
				path = "";
			if (null != cast) {
				URI uri = new URI("http://" + remoteAddress.getHostString()
						+ ":" + remoteAddress.getPort()
						+ path);
				return httpMessageFactory.makeHttpMessage(cast, uri);
			}
		} catch (URISyntaxException e) {
			if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return null;
	}

}
