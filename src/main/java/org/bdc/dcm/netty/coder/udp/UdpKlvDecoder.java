package org.bdc.dcm.netty.coder.udp;

import org.bdc.dcm.data.coder.KlvDecoder;
import org.bdc.dcm.data.coder.udp.DataUdpDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpKlvDecoder extends UdpDecoder {

	final static Logger logger = LoggerFactory.getLogger(UdpKlvDecoder.class);
	
	public UdpKlvDecoder(NettyBoot nettyBoot) {
		super(logger, nettyBoot, new DataUdpDecoder(new KlvDecoder()));
	}
	
}
