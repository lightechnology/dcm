package org.bdc.dcm.netty.coder.tcp;

import org.bdc.dcm.data.coder.LqmdbDecoder;
import org.bdc.dcm.data.coder.tcp.DataTcpDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

public class TcpLqmdbDecoder extends TcpDecoder {

final static Logger logger = LoggerFactory.getLogger(TcpLqmdbDecoder.class);
	
	public TcpLqmdbDecoder(NettyBoot nettyBoot) {	
		super(logger, nettyBoot, new DataTcpDecoder<ByteBuf>(new LqmdbDecoder()));
	}
}
