package org.bdc.dcm.netty.coder.udp;

import java.net.SocketAddress;
import org.bdc.dcm.data.coder.LcmdbEncoder;
import org.bdc.dcm.data.coder.udp.DataUdpEncoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpLcmdbEncoder extends UdpEncoder {
	
	final static Logger logger = LoggerFactory.getLogger(UdpLcmdbEncoder.class);
	
	public UdpLcmdbEncoder(SocketAddress remoteAddress, NettyBoot nettyBoot) {
		super(logger, nettyBoot, new DataUdpEncoder(new LcmdbEncoder()));
		setRemoteAddress(remoteAddress);
	}

}
