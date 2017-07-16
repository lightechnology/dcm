package org.bdc.dcm.netty.coder.tcp;

import io.netty.buffer.ByteBuf;

import org.bdc.dcm.data.coder.LcmdbDecoder;
import org.bdc.dcm.data.coder.tcp.DataTcpDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.e.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpLcmdbDecoder extends TcpDecoder {

	final static Logger logger = LoggerFactory.getLogger(TcpLcmdbDecoder.class);
	
	public TcpLcmdbDecoder(NettyBoot nettyBoot,DataType dataType) {
		super(logger, nettyBoot, new DataTcpDecoder<ByteBuf>(new LcmdbDecoder(dataType)));
	}

}
