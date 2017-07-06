package org.bdc.dcm.netty.framer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bdc.dcm.conf.ComConf;
import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.vo.DataTab;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class JmstrFrameDecoder extends ByteToMessageDecoder {
    
    private static Map<Integer, Integer> lenCached = new HashMap<Integer, Integer>();
    private final DataTabConf dataTabConf;
    
    public JmstrFrameDecoder() {
        this.dataTabConf = IntfConf.getDataTabConf();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
            List<Object> out) throws Exception {
        String charset = ComConf.getInstance().CHARSET;
        while (in.isReadable()) {
            in.markReaderIndex();
            if (5 < in.readableBytes()) {
                byte[] data = new byte[6];
                in.readBytes(data);
                String str = new String(data, charset);
                Pattern pattern = Pattern.compile("^J\\d{4}_$");
                Matcher matcher = pattern.matcher(str);
                if (!matcher.find()) {
                    in.resetReaderIndex();
                    in.readByte();
                    continue;
                }
                if (5 < in.readableBytes()) {
                    in.readBytes(data);
                    str = new String(data, charset);
                    pattern = Pattern.compile("^00(([A-Z]0)|(0[A-Z]))[1-9]_$");
                    matcher = pattern.matcher(str);
                    if (!matcher.find()) {
                        in.resetReaderIndex();
                        in.readByte();
                        continue;
                    }
                    int len = 0;
                    int kind = Public.objToInt(str.substring(4, 5));
                    if (lenCached.containsKey(kind))
                        len = lenCached.get(kind);
                    else {
                        List<DataTab> list = dataTabConf.getDataTabConf("jm");
                        if (null != list && !list.isEmpty()) {
                            for (int i = 0; i < list.size(); i++) {
                                DataTab dataTab = list.get(i);
                                if (kind == dataTab.getKind())
                                    len++;
                            }
                            lenCached.put(kind, len);
                        }
                    }
                    if (0 == len) {
                        in.resetReaderIndex();
                        in.readByte();
                        continue;
                    }
                    // 通过dt找出应该的字符长度
                    len = len * 6 - 1;
                    if (len + 5 < in.readableBytes()) {
                        in.resetReaderIndex();
                        data = new byte[len + 2 * 6];
                        in.readBytes(data);
                        String crc = "00000" + Public.bytes2Int(Public.crc16_A001(data));
                        crc = "_" + crc.substring(crc.length() - 5);
                        data = new byte[6];
                        in.readBytes(data);
                        if (!crc.equals(new String(data, charset))) {
                            in.resetReaderIndex();
                            in.readByte();
                            continue;
                        }
                        len = len + 3 * 6;
                        in.resetReaderIndex();
                        ByteBuf frame = ctx.alloc().buffer(len);
                        frame.writeBytes(in, in.readerIndex(), len);
                        in.readerIndex(in.readerIndex() + len);
                        out.add(frame);
                    } else
                        break;
                } else
                    break;
            } else
                break;
        }
    }

}
