package org.bdc.dcm.netty.framer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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

	private CheckManage manage ;
	
	public LcmdbFrameDecoder() {
		super();
		manage = new CheckManage();
		manage.addInterceptor(new FixBytesCheckInterceptor(new byte[] {(byte)0xfe,(byte)0xa5}));
		manage.addInterceptor(new LenCheckInterceptor(1, 1));
		manage.addInterceptor(new checkSumInterceptor());
	}

	@Override
	//从type 开始到校验码之前的所有字节累加
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
            List<Object> out) throws Exception {
        // 有足够数据时
        while (in.isReadable() && 1 < in.readableBytes()) {
            // 标记当前位置，以便reset
           if( manage.check(in)) {
        	   	ByteBuf tmpBuf = in.copy(0, in.readerIndex());
	       		byte[] tmpBytes = new byte[tmpBuf.readableBytes()];
	       		tmpBuf.readBytes(tmpBytes);
	       		logger.error("校验过的：{},{}",ctx.channel().id(),Public.byte2hex(tmpBytes));
	       		ByteBuf msg = in.alloc().buffer();
	       		msg.writeBytes(Public.hexString2bytes("FE A5 01 12 16 00 12 4B 00 0A DC 89 5B 00 01 03 00 80 00 0F 04 26 0D"));
	       		ctx.writeAndFlush(msg);
           }	
        }
    }
}
