package org.bdc.dcm.netty.framer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 用于拆包粘包
 * @author Administrator
 *
 */
public class LqmdbFrameDecoder extends ByteToMessageDecoder {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private int macLen = 6;
	
	private int modBusHeadLen = 3;
	
	private int crcLen = 2;
	
	/**
	 * 两种形式的包
	 * 1.查询回复包 长度标志
	 * 2.写回复包 与发送内容相同
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		while (in.isReadable()) {
			if(logger.isDebugEnabled())
				logger.debug("新一轮可读长度：{}",in.readableBytes());
			
			if(in.readableBytes() > (macLen + modBusHeadLen-1)){
				in.markReaderIndex();
				byte[] head = new byte[macLen+modBusHeadLen];
				in.readBytes(head);
				int contentLen = head[head.length - 1 ] & 0xff;
				in.resetReaderIndex();
				byte[] macbytes = new byte[macLen];
				int packLen = modBusHeadLen+contentLen+crcLen;
				if((packLen + macLen) <= in.readableBytes()){//防止包过长
					byte[] packBytes = new byte[packLen];
					in.readBytes(macbytes);
					in.readBytes(packBytes);
					if(logger.isDebugEnabled())
						logger.debug("crc之前的长度：{}",in.readableBytes());
					if(Public.bytes2Int(Public.crc16_A001(packBytes)) == 0){
						in.resetReaderIndex();
						out.add(in.readBytes(macLen+modBusHeadLen+contentLen+crcLen));
						if(logger.isDebugEnabled())
							logger.debug("检验成功");
					}else{//crc 校验不过
						in.resetReaderIndex();
						in.readByte();
						if(logger.isDebugEnabled())
							logger.debug("crc 检验不过，可读的字节：{}",in.readableBytes());
					}
				}else{
					in.resetReaderIndex();
					in.readByte();
				}
			}else{
				if(logger.isDebugEnabled())
					logger.debug("长度不够");
				break;
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("解析完成");
	}

}
