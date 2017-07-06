package org.bdc.dcm.netty.coder.http;

import org.bdc.dcm.data.coder.JmjsnDecoder;
import org.bdc.dcm.data.coder.http.DataHttpDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpJmjsnDecoder extends HttpDecoder {
	
	final static Logger logger = LoggerFactory.getLogger(HttpJmjsnDecoder.class);
	
	public HttpJmjsnDecoder(NettyBoot nettyBoot) {
		super(logger, nettyBoot, new DataHttpDecoder(new JmjsnDecoder(nettyBoot)));
	}
	
}