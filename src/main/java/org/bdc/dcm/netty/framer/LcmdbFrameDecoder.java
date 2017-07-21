package org.bdc.dcm.netty.framer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.bdc.dcm.comm.callback.CheckManage;
import org.bdc.dcm.comm.callback.FixBytesCheckInterceptor;
import org.bdc.dcm.comm.callback.LenCheckInterceptor;
import org.bdc.dcm.comm.callback.checkSumInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class LcmdbFrameDecoder extends ByteToMessageDecoder {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 组合的方式 减少依赖
	private CheckManage manage;

	public LcmdbFrameDecoder() {
		super();
		manage = new CheckManage();
		manage.addInterceptor(new FixBytesCheckInterceptor(new byte[] { (byte) 0xfe, (byte) 0xa5 }));
		manage.addInterceptor(new checkSumInterceptor());
		manage.addInterceptor(new LenCheckInterceptor(3, 1));
	}

	@Override
	// 从type 开始到校验码之前的所有字节累加
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		// 标记当前位置，以便reset
		ByteBuf buf = manage.check(in);
		if (buf != null) {
			byte[] tmpBytes = new byte[buf.readableBytes()];
			buf.readBytes(tmpBytes);
			out.add(buf);
			logger.error("----------------------------------------------------------校验过的：{},可读长度:：{}----------------------------------------------------------", Public.byte2hex(tmpBytes),in.readableBytes());
		}else {
        	in.readBytes(in.readableBytes());
		}

	}
}
