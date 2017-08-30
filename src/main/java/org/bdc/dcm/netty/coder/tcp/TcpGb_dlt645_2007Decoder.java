package org.bdc.dcm.netty.coder.tcp;

import org.bdc.dcm.data.coder.Gb_dlt645_2007Decoder;
import org.bdc.dcm.data.coder.tcp.DataTcpDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

public class TcpGb_dlt645_2007Decoder extends TcpDecoder  {

	final static Logger logger = LoggerFactory.getLogger(TcpGb_dlt645_2007Decoder.class);
	
	public TcpGb_dlt645_2007Decoder(NettyBoot nettyBoot) {
		super(logger, nettyBoot, new DataTcpDecoder<ByteBuf>(new Gb_dlt645_2007Decoder()));
	}

}
