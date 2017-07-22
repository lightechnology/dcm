package org.bdc.dcm.netty.coder.tcp;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import org.bdc.dcm.data.coder.JmstrEncoder;
import org.bdc.dcm.data.coder.tcp.DataTcpEncoder;
import org.bdc.dcm.data.log.String4Log;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.coder.Encoder;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpJmstrEncoder extends Encoder<DataPack, String> {

	final static Logger logger = LoggerFactory.getLogger(TcpJmstrEncoder.class);

	public TcpJmstrEncoder(NettyBoot nettyBoot) {
		super(new String4Log(TAG, logger), nettyBoot,
				new DataTcpEncoder<String>(new JmstrEncoder()));
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, DataPack msg,
			List<Object> out) {
		doEncode(ctx, msg, out);
	}

}