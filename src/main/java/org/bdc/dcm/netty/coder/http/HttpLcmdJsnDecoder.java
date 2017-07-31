package org.bdc.dcm.netty.coder.http;

import org.bdc.dcm.data.coder.LcmdbJsnDecoder;
import org.bdc.dcm.data.coder.http.DatasHttpDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpLcmdJsnDecoder extends HttpDecoder {
	
	final static Logger logger = LoggerFactory.getLogger(HttpLcmdJsnDecoder.class);
	
	public HttpLcmdJsnDecoder(NettyBoot nettyBoot) {
		super(logger, nettyBoot, new DatasHttpDecoder(new LcmdbJsnDecoder(nettyBoot)));
	}
	
}