package org.bdc.dcm.netty.framer;

import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class StringFrameDecoder extends ByteToMessageDecoder {

    private int length = 0;

    public StringFrameDecoder(int length) {
        this.length = length;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (length > in.readableBytes())
            return;
        out.add(in.readBytes(length));
    }

}
