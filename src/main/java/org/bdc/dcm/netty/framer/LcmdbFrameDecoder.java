package org.bdc.dcm.netty.framer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.bdc.dcm.comm.callback.CheckManage;
import org.bdc.dcm.comm.callback.FixBytesCheckInterceptor;
import org.bdc.dcm.comm.callback.WindowFixCheckInterceptorImpl;
import org.bdc.dcm.comm.callback.checkSumInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class LcmdbFrameDecoder extends ByteToMessageDecoder {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 组合的方式 减少依赖
	private CheckManage checkManage;

	public LcmdbFrameDecoder() {
		super();
		checkManage = new CheckManage();
		checkManage.addInterceptor(new FixBytesCheckInterceptor(new byte[] { (byte) 0xfe, (byte) 0xa5 }));
		checkManage.addInterceptor(new WindowFixCheckInterceptorImpl(3, 1));
		checkManage.addInterceptor(new checkSumInterceptor());
		
	}

	@Override
	/**
	 * 如果缓冲区有值 那么会轮训这个方法
	 */
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		ByteBuf buf = checkManage.check(in,out);
		if(buf.isReadable()) {
			buf.markReaderIndex();
			byte[] bs = new byte[buf.readableBytes() - buf.readerIndex()];
			buf.getBytes(buf.readerIndex(),bs);
			logger.info("通过数据：{}",Public.byte2hex(bs));
			out.add(buf);
		}
	}
}
