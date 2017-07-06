package org.bdc.dcm.netty.framer;

import java.util.List;

import org.bdc.dcm.conf.ComConf;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class KlvFrameDecoder extends ByteToMessageDecoder {
    

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		// 有数据时
		while (in.isReadable()) {
			// 记住第一个位置
			int pos = in.readerIndex();
			// 标记当前位置，以便reset
			in.markReaderIndex();
			// 判断是否是8F开头
			if ((byte) 0x8F == in.readByte()) {
				// 判断够不够取数据包长度标识
				if (1 < in.readableBytes()) {
					byte[] len = { in.readByte(), in.readByte() };
					int ln = Public.bytes2Int(len);
					if (ln < in.readableBytes()
							&& (byte) 0x8E == in.getByte(pos + 3 + ln)) {
						byte[] data = new byte[ln + 2];
						in.readerIndex(pos + 1);
						in.readBytes(data);
						// crc校验成功，切取整个数据包
						if (0 == Public.bytes2Int(Public.crc16(data))) {
							in.resetReaderIndex();
							ByteBuf frame = ctx.alloc().buffer(4 + ln);
							frame.writeBytes(in, in.readerIndex(), 4 + ln);
							in.readerIndex(in.readerIndex() + 4 + ln);
							out.add(frame);
						}
						// crc校验不过，回到起始位置后一个的位置，即跳过开头判断等于8F的数据
						else {
							in.readerIndex(pos + 1);
						}
					}
					// 长度超出预设，是错误数据，跳过第一个字节往后判断
					else if (ln > Public.objToInt(ComConf.getInstance().DATAPACK_MAXLENGTH))
						in.readerIndex(pos + 1);
					// 长度不够，回到数据起始位置，这里不用写return，继续运行下去就是“return false”这句
					else if (ln >= in.readableBytes()) {
						in.resetReaderIndex();
						break;
					}
					// 长度够，就时最后一个字节不时8E，跳过第一个字节往后判断
					else
						in.readerIndex(pos + 1);
				}
				// 长度不够，回到数据起始位置，这里不用写return，继续运行下去就是“return false”这句
				else {
					in.resetReaderIndex();
					break;
				}
			}
		}
	}

}
