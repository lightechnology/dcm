package org.bdc.dcm.netty.coder.http;

import org.bdc.dcm.data.coder.JmjsnEncoder;
import org.bdc.dcm.data.coder.http.DataHttpEncoder;
import org.bdc.dcm.data.coder.intf.HttpMessageFactory;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpJmjsnEncoder extends HttpEncoder {

	final static Logger logger = LoggerFactory.getLogger(HttpJmjsnEncoder.class);
	
	public HttpJmjsnEncoder(NettyBoot nettyBoot, HttpMessageFactory httpMessageFactory) {
		super(logger, nettyBoot, new DataHttpEncoder(httpMessageFactory, nettyBoot, new JmjsnEncoder(nettyBoot)));
	}

}