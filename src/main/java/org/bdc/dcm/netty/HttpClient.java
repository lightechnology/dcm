package org.bdc.dcm.netty;

import java.util.concurrent.Executors;

import org.bdc.dcm.conf.ComConf;
import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.vo.Server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class HttpClient extends NettyBoot {
	
	private Bootstrap bootstrap;

	public HttpClient(Server server) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		super(server);
		this.fixedThreadPool = Executors.newFixedThreadPool(ComConf.getInstance().THREAD_POOL_SIZE);
		init();
	}

	private void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		setWorkerGroup(new NioEventLoopGroup());
		bootstrap = new Bootstrap();
		bootstrap.group(getWorkerGroup()).channel(NioSocketChannel.class)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
					.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024);
		String classUri = "org.bdc.dcm.netty.channel.http.HttpClient" + getServer().getDataType() + "ChannelInitializer";
		Class<?> classType = Class.forName(classUri);
		@SuppressWarnings("unchecked")
		AbstractChannelInitializer<SocketChannel> channelInitializer = (AbstractChannelInitializer<SocketChannel>) classType.newInstance();
		channelInitializer.setNettyBoot(this);
		bootstrap.handler(channelInitializer);
	}

	@Override
	public void shutdown() {
		super.shutdown();
	}

	@Override
	public void task() throws Exception {
        invalidHost();
		if (null != getServer().getClientPort() && 0 < getServer().getClientPort())
			bootstrap.bind(getServer().getClientPort());
		setChannelFuture(bootstrap.connect(getServer().getHost(),getServer().getServerPort()).sync());
		getChannelFuture().channel().closeFuture().sync();
	}

}
