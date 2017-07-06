package org.bdc.dcm.netty.coder.http;

import org.bdc.dcm.data.coder.XarjsnDecoder;
import org.bdc.dcm.data.coder.http.DataHttpDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpXarjsnDecoder extends HttpDecoder {
	
	final static Logger logger = LoggerFactory.getLogger(HttpXarjsnDecoder.class);
	
	public HttpXarjsnDecoder(NettyBoot nettyBoot) {
		super(logger, nettyBoot, new DataHttpDecoder(new XarjsnDecoder(nettyBoot)));
	}
	
}