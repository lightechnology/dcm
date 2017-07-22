package org.bdc.dcm.netty.channel.udp;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.udp.UdpKlvDecoder;
import org.bdc.dcm.netty.coder.udp.UdpKlvEncoder;
import org.bdc.dcm.netty.handler.DataHandler;

public class UdpKlvChannelInitializer extends AbstractChannelInitializer<DatagramChannel> {
	
	private SocketAddress remoteAddress;
	
	public UdpKlvChannelInitializer(String ip, Integer port) {
		if (null != port && 0 < port) {
			if (invalidHost(ip))
				this.remoteAddress = new InetSocketAddress("255.255.255.255", port);
			else
				this.remoteAddress = new InetSocketAddress(ip, port);
		} else
			this.remoteAddress = null;
	}

	@Override
	protected void initChannel(DatagramChannel ch) throws Exception {
		super.initChannel(ch);
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("decoder", new UdpKlvDecoder(getNettyBoot()));
		pipeline.addLast("encoder", new UdpKlvEncoder(remoteAddress, getNettyBoot()));
		pipeline.addLast("dataHandler", new DataHandler(getNettyBoot()));
	}

}
