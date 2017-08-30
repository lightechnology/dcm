package org.bdc.dcm.netty.coder.tcp;

import org.bdc.dcm.data.coder.YgdqmdbDecoder;
import org.bdc.dcm.data.coder.tcp.DataTcpDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

public class TcpYgdqmdbDecoder extends TcpDecoder {

	final static Logger logger = LoggerFactory.getLogger(TcpYgdqmdbDecoder.class);
	
	public TcpYgdqmdbDecoder(NettyBoot nettyBoot) {
		super(logger, nettyBoot, new DataTcpDecoder<ByteBuf>(new YgdqmdbDecoder()));
	}

}
