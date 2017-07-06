package org.bdc.dcm.netty.channel.tcp;

import java.nio.charset.Charset;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.bdc.dcm.conf.ComConf;
import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.tcp.TcpJmstrDecoder;
import org.bdc.dcm.netty.coder.tcp.TcpJmstrEncoder;
import org.bdc.dcm.netty.framer.JmstrFrameDecoder;
import org.bdc.dcm.netty.handler.DataHandler;

public class TcpJmstrChannelInitializer extends AbstractChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		super.initChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("framer", new JmstrFrameDecoder());
		pipeline.addLast("decoder", new StringDecoder(Charset.forName(ComConf.getInstance().CHARSET)));
		pipeline.addLast("cusDecoder", new TcpJmstrDecoder(getNettyBoot()));
		pipeline.addLast("encoder", new StringEncoder(Charset.forName(ComConf.getInstance().CHARSET)));
		pipeline.addLast("cusEncoder", new TcpJmstrEncoder(getNettyBoot()));
		pipeline.addLast("dataHandler", new DataHandler(getNettyBoot()));
	}

}
