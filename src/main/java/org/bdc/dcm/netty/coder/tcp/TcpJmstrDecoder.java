package org.bdc.dcm.netty.coder.tcp;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;

import org.bdc.dcm.data.coder.JmstrDecoder;
import org.bdc.dcm.data.coder.tcp.DataTcpDecoder;
import org.bdc.dcm.data.log.String4Log;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.coder.Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpJmstrDecoder extends Decoder<String> {

	final static Logger logger = LoggerFactory.getLogger(TcpJmstrDecoder.class);

	public TcpJmstrDecoder(NettyBoot nettyBoot) {
		super(new String4Log(TAG, logger), nettyBoot,
				new DataTcpDecoder<String>(new JmstrDecoder()));
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, String msg,
			List<Object> out) throws Exception {
		doDecode(ctx, msg, out);
	}

}
