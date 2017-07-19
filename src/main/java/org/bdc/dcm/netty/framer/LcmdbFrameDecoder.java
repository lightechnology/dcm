package org.bdc.dcm.netty.framer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.bdc.dcm.comm.checkSumInterceptor;
import org.bdc.dcm.comm.callback.CheckManage;
import org.bdc.dcm.comm.callback.FixBytesCheckInterceptor;
import org.bdc.dcm.comm.callback.LenCheckInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class LcmdbFrameDecoder extends ByteToMessageDecoder {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private CheckManage manage ;
	
	public LcmdbFrameDecoder() {
		super();
		manage = new CheckManage();
		manage.addInterceptor(new FixBytesCheckInterceptor(new byte[] {(byte)0xfe,(byte)0xa5}));
		manage.addInterceptor(new checkSumInterceptor());
		manage.addInterceptor(new LenCheckInterceptor(1, 1));
	}

	@Override
	//从type 开始到校验码之前的所有字节累加
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
            List<Object> out) throws Exception {
        // 有足够数据时
        while (in.isReadable() && 1 < in.readableBytes()) {
            // 标记当前位置，以便reset
           if( manage.check(in)) {
        	   	ByteBuf tmpBuf = in.resetReaderIndex().alloc().buffer(in.readableBytes()).writeBytes(in);
	       		byte[] tmpBytes = new byte[tmpBuf.readableBytes()];
	       		tmpBuf.readBytes(tmpBytes);
	       		logger.error("校验过的：{}",Public.byte2hex(tmpBytes),tmpBuf.readableBytes());
           }	
        }
    }
}
