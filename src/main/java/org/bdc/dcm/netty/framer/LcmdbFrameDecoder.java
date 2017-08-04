package org.bdc.dcm.netty.framer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.bdc.dcm.conf.ComConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class LcmdbFrameDecoder extends ByteToMessageDecoder {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		while (in.isReadable() && 3 < in.readableBytes()) {
			// 标记当前位置，以便reset
			in.markReaderIndex();
			// 判断是否是FE A5开头
			if ((byte) 0xFE == in.readByte() && (byte) 0xA5 == in.readByte()) {
				byte type = in.readByte();
				int payloadLen = in.readByte() & 0xff;
				if (payloadLen <= in.readableBytes()) {
					int sum = (type + payloadLen) & 0xff;// 不用byte存和 应为Java是有符号的
					byte[] playLoadBytes = new byte[payloadLen];
					byte[] subPlayLoadBytes = null;
					for (int i = 0; i < payloadLen; i++) {
						byte b = in.readByte();
						sum = (sum + b) & 0xff;
						if (i > 9) {// Command+8mac+order
							if (subPlayLoadBytes == null) subPlayLoadBytes = new byte[payloadLen - 10];
							subPlayLoadBytes[i - 10] = b;
						}
						playLoadBytes[i] = b;
					}
					int packSumCheck = in.readByte()&0xff;//不要用byte 因为存在符号
					// 数据包校验和、数据CRC校验
					if (sum == packSumCheck && playLoadBytes[0] == (byte) 0x0c && payloadLen == 9) {// Command+8mac
						toDecoder(ctx, in, out, payloadLen);
					} else if (sum == packSumCheck && playLoadBytes[0] == (byte) 0x17 && payloadLen > 14 && Public.bytes2Int(Public.crc16_A001(subPlayLoadBytes)) == 0) {// Command+8mac+order+addr+funcode+XXXX+2crc状态信息报
						toDecoder(ctx, in, out, payloadLen);
					} else {
						in.resetReaderIndex();
						in.readByte();
					}
					// 避免解析长度数据错误得到一个过长的值，那永远判断不过
				} else if (payloadLen > Public.objToInt(ComConf.getInstance().DATAPACK_MAXLENGTH)) {
					in.resetReaderIndex();
					in.readByte();
				} else {
					in.resetReaderIndex();
					break;
				}
			} else {
				in.resetReaderIndex();
				in.readByte();
			}
		}
	}

	private void toDecoder(ChannelHandlerContext ctx, ByteBuf in, List<Object> out, int payloadLen) {
		int aLen = 4 + payloadLen + 1;
		in.resetReaderIndex();
		ByteBuf frame = ctx.alloc().buffer(aLen);
		frame.writeBytes(in, in.readerIndex(), aLen);
		out.add(frame);
		in.readerIndex(in.readerIndex() + aLen);
	}
}
