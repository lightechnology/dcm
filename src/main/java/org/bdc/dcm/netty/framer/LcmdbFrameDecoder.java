package org.bdc.dcm.netty.framer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.bdc.dcm.conf.ComConf;

import com.util.tools.Public;

public class LcmdbFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
            List<Object> out) throws Exception {
        // 有足够数据时
        while (in.isReadable() && 1 < in.readableBytes()) {
            // 标记当前位置，以便reset
            in.markReaderIndex();
            // 判断是否是FE A5开头
            if ((byte) 0xFE == in.readByte() && (byte) 0xA5 == in.readByte()) {
                // 判断够不够取数据包长度标识
                if (1 < in.readableBytes()) {
                    int dt = in.readInt();
                    int ln = in.readInt();
                    if (ln < in.readableBytes()) {
                        byte ebyte = (byte) (dt + ln);
                        byte[] dl = new byte[ln - 10];
                        for (int i = 0; i < ln; i++) {
                            byte b = in.readByte();
                            ebyte += b;
                            if (9 < i)
                                dl[i - 10] = b;
                        }
                        // 数据包校验和、数据CRC校验
                        if (ebyte == in.readByte()
                                && 0 == Public.bytes2Int(Public.crc16_A001(dl))) {
                            in.resetReaderIndex();
                            ByteBuf frame = ctx.alloc().buffer(5 + ln);
                            frame.writeBytes(in, in.readerIndex(), 5 + ln);
                            in.readerIndex(in.readerIndex() + 5 + ln);
                            out.add(frame);
                        }
                        // 校验不过，跳过头部字节往后判断
                        else {
                            in.resetReaderIndex();
                            in.readByte();
                            in.readByte();
                        }
                    }
                    // 如果长度超出预设的，认为错误数据，跳过
                    else if (ln > Public.objToInt(ComConf.getInstance().DATAPACK_MAXLENGTH)) {
                        in.resetReaderIndex();
                        in.readByte();
                        in.readByte();
                    }
                    // 长度不够，回到数据起始位置，这里不用写return，继续运行下去就是“return false”这句
                    else {
                        in.resetReaderIndex();
                        break;
                    }
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
