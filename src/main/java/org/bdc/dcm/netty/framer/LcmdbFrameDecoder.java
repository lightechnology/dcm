package org.bdc.dcm.netty.framer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.ArrayList;
import java.util.List;

import org.bdc.dcm.comm.CheckService;
import org.bdc.dcm.comm.callback.CheckInterceptor;
import org.bdc.dcm.comm.callback.LenCheckInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;



public class LcmdbFrameDecoder extends ByteToMessageDecoder {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	//从type 开始到校验码之前的所有字节累加
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
            List<Object> out) throws Exception {
        // 有足够数据时
        while (in.isReadable() && 1 < in.readableBytes()) {
            // 标记当前位置，以便reset
            in.markReaderIndex();
            // 判断是否是FE A5开头
            if ((byte) 0xFE == in.readByte() && (byte) 0xA5 == in.readByte()) {
            	List<CheckInterceptor> cbs = new ArrayList<>();
            	cbs.add(new LenCheckInterceptor(1,1));//第一位是type
            	if(CheckService.crcCheckSum(in.alloc().buffer(in.readableBytes()).writeBytes(in),cbs)){
            		ByteBuf tmpBuf = in.resetReaderIndex().alloc().buffer(in.readableBytes()).writeBytes(in);
            		byte[] tmpBytes = new byte[tmpBuf.readableBytes()];
            		tmpBuf.readBytes(tmpBytes);
            		logger.error("校验过的：{}",Public.byte2hex(tmpBytes),tmpBuf.readableBytes());
            	}
            }
        }
    }
}
