package org.bdc.dcm.netty.coder.tcp;

import org.bdc.dcm.data.coder.LcmdbEncoder;
import org.bdc.dcm.data.coder.tcp.DataTcpEncoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

public class TcpLcmdbEncoder extends TcpEncoder {

	final static Logger logger = LoggerFactory.getLogger(TcpLcmdbEncoder.class);
	
	public TcpLcmdbEncoder(NettyBoot nettyBoot) {
		super(logger, nettyBoot, new DataTcpEncoder<ByteBuf>(new LcmdbEncoder()));
	}
	
}