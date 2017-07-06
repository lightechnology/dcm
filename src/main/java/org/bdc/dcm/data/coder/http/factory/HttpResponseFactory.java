package org.bdc.dcm.data.coder.http.factory;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.bdc.dcm.conf.ComConf;
import org.bdc.dcm.data.coder.intf.HttpMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponseFactory implements HttpMessageFactory {

	final static Logger logger = LoggerFactory
			.getLogger(HttpResponseFactory.class);

	@Override
	public HttpContent makeHttpMessage(String msg, URI uri) {
		try {
			FullHttpResponse response = new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(msg.getBytes(ComConf.getInstance().CHARSET)));
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);
			response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,
					response.content().readableBytes());
			response.headers().set(HttpHeaderNames.CONNECTION,
					HttpHeaderValues.KEEP_ALIVE);
			return response;
		} catch (UnsupportedEncodingException e) {
			if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return null;
	}

}
