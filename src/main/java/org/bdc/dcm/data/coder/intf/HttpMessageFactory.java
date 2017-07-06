package org.bdc.dcm.data.coder.intf;

import java.net.URI;

import io.netty.handler.codec.http.HttpContent;

public interface HttpMessageFactory {
	
	public HttpContent makeHttpMessage(String msg, URI uri);

}
