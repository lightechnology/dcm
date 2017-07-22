package org.bdc.dcm.netty.coder;

import java.util.List;

import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.data.log.intf.Coder4Log;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public abstract class Encoder<T, I> extends MessageToMessageEncoder<T> {
	
	public final static String TAG = "Sending connection to: {} -- data: {} || map: {}";

    private EncoderUtilTools<I> encoderUtilTools;
    
	public Encoder(Coder4Log<I> coder4Log, NettyBoot nettyBoot, DataEncoder<I> encoder) {
        this.encoderUtilTools = new EncoderUtilTools<I>(coder4Log, encoder, nettyBoot);
	}

	public void doEncode(ChannelHandlerContext ctx, DataPack msg, List<Object> out) {
	    encoderUtilTools.encode(ctx, msg, out);
	}
}
