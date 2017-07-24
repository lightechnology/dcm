package org.bdc.dcm.netty.coder.http;

import org.bdc.dcm.data.coder.LcmdbJsn1Encoder;
import org.bdc.dcm.data.coder.LcmdbJsnEncoder;
import org.bdc.dcm.data.coder.http.DataHttpEncoder;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.data.coder.intf.HttpMessageFactory;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpContent;

public class HttpClientLcmdJsnEncoder extends HttpEncoder  {

	final static Logger logger = LoggerFactory.getLogger(HttpLcmdJsnDecoder.class);
	
	public HttpClientLcmdJsnEncoder(NettyBoot nettyBoot, HttpMessageFactory httpMessageFactory) {
		super(logger, nettyBoot, new DataHttpEncoder(httpMessageFactory, nettyBoot, new LcmdbJsn1Encoder(nettyBoot)));
	}	

}
