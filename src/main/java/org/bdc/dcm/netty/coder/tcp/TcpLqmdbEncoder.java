package org.bdc.dcm.netty.coder.tcp;

import org.bdc.dcm.data.coder.LqmdbEncoder;
import org.bdc.dcm.data.coder.tcp.DataTcpEncoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

public class TcpLqmdbEncoder extends TcpEncoder {

	final static Logger logger = LoggerFactory.getLogger(TcpLqmdbEncoder.class);
	
	public TcpLqmdbEncoder(NettyBoot nettyBoot) {
		super(logger, nettyBoot, new DataTcpEncoder<ByteBuf>(new LqmdbEncoder()));
	}

}
