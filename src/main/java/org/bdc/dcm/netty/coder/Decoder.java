package org.bdc.dcm.netty.coder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.log.intf.Coder4Log;
import org.bdc.dcm.netty.NettyBoot;

public abstract class Decoder<T> extends MessageToMessageDecoder<T> {

	public final static String TAG = "Accepted connection from: {} -- data: {} || map: {}";

    private DecoderUtilTools<T> decoderUtilTools;
    
	public Decoder(Coder4Log<T> coder4Log, NettyBoot nettyBoot, DataDecoder<T> decoder) {
	    this.decoderUtilTools = new DecoderUtilTools<T>(coder4Log, decoder, nettyBoot);
	}

	public void doDecode(ChannelHandlerContext ctx, T msg, List<Object> out) {
	    decoderUtilTools.decode(ctx, msg, out);
	}

}
