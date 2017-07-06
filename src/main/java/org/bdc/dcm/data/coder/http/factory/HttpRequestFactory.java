package org.bdc.dcm.data.coder.http.factory;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bdc.dcm.data.coder.intf.HttpMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder.ErrorDataEncoderException;

public class HttpRequestFactory implements HttpMessageFactory {
	
	final static Logger logger = LoggerFactory.getLogger(HttpRequestFactory.class);

	@Override
	public HttpContent makeHttpMessage(String msg, URI uri) {
		try {
			/*FullHttpRequest request = new DefaultFullHttpRequest(
					HttpVersion.HTTP_1_1, HttpMethod.POST, uri.toASCIIString(),
					Unpooled.wrappedBuffer(msg.getBytes(ComConf.getInstance().CHARSET)));
			// 构建http请求
			request.headers().set(HttpHeaderNames.HOST, uri.getHost());
			request.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);
			request.headers().set(HttpHeaderNames.CONNECTION,
					HttpHeaderValues.KEEP_ALIVE);
			request.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,
					request.content().readableBytes());*/
			HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getPath());
			HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
			HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(factory, request, false);
			Map<String, Object> paramters = Public.getParamsFromURL(msg);
			Iterator<Entry<String, Object>> ite = paramters.entrySet().iterator();
			while (ite.hasNext()) {
				Map.Entry<String, Object> entry = ite.next();
				bodyRequestEncoder.addBodyAttribute(entry.getKey(), entry.getValue().toString());
			}
			request = bodyRequestEncoder.finalizeRequest();
			request.headers().set(HttpHeaderNames.HOST, uri.getHost());
			request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			request.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);
			return (HttpContent) request;
		} catch (ErrorDataEncoderException e) {
			if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return null;
	}

}
