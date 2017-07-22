package org.bdc.dcm.netty.channel.udp;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.netty.coder.udp.UdpLcmdbDecoder;
import org.bdc.dcm.netty.coder.udp.UdpLcmdbEncoder;
import org.bdc.dcm.netty.handler.DataHandler;

public class UdpLcmdbChannelInitializer extends AbstractChannelInitializer<DatagramChannel> {
	
	private SocketAddress remoteAddress;
	
	public UdpLcmdbChannelInitializer(String ip, Integer port) {
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
		pipeline.addLast("decoder", new UdpLcmdbDecoder(getNettyBoot()));
		pipeline.addLast("encoder", new UdpLcmdbEncoder(remoteAddress, getNettyBoot()));
		pipeline.addLast("dataHandler", new DataHandler(getNettyBoot()));
	}

}
