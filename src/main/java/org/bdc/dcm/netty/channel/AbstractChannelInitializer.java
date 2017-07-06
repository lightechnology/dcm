package org.bdc.dcm.netty.channel;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.coder.exception.ExceptionCaught;

import com.util.tools.Public;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

public abstract class AbstractChannelInitializer<C extends Channel> extends ChannelInitializer<C> {

	private NettyBoot nettyBoot;

	public NettyBoot getNettyBoot() {
		return nettyBoot;
	}

	public void setNettyBoot(NettyBoot nettyBoot) {
		this.nettyBoot = nettyBoot;
	}
	
	public boolean invalidHost(String host) {
		Pattern pattern = Pattern
				.compile("^(?:(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))$");
		return null == host || 0 == host.length()
				|| !pattern.matcher(host).matches();
	}

	@Override
	protected void initChannel(C ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("exception", new ExceptionCaught());
		boolean d = true;
		if (d && 0 < Public.objToInt(nettyBoot.getServer().getKeepAlive())) {
			long t = nettyBoot.getServer().getKeepAlive();
			pipeline.addLast("ping", new IdleStateHandler(t, t, 2 * t,TimeUnit.SECONDS));
		}
	}
	
}
