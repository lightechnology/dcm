package org.bdc.dcm.netty.coder.udp;

import org.bdc.dcm.data.coder.LcmdbDecoder;
import org.bdc.dcm.data.coder.udp.DataUdpDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpLcmdbDecoder extends UdpDecoder {

	final static Logger logger = LoggerFactory.getLogger(UdpLcmdbDecoder.class);
	
	public UdpLcmdbDecoder(NettyBoot nettyBoot) {
		super(logger, nettyBoot, new DataUdpDecoder(new LcmdbDecoder()));
	}
	
}
