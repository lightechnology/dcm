package org.bdc.dcm.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.vo.Server;

public class HttpServer extends NettyBoot {
	
	private static final int SO_BACKLOG = 128;

	private ServerBootstrap serverBootstrap;

	public HttpServer(Server server) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		super(server);
		init();
	}

	private void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		setBossGroup(new NioEventLoopGroup());
		setWorkerGroup(new NioEventLoopGroup());
		serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(getBossGroup(), getWorkerGroup())
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, SO_BACKLOG)
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
		String classUri = "org.bdc.dcm.netty.channel.http.HttpServer" + getServer().getDataType() + "ChannelInitializer";
		Class<?> classType = Class.forName(classUri);
		@SuppressWarnings("unchecked")
		AbstractChannelInitializer<SocketChannel> channelInitializer = (AbstractChannelInitializer<SocketChannel>) classType.newInstance();
		channelInitializer.setNettyBoot(this);
		serverBootstrap.childHandler(channelInitializer);
	}

	@Override
	public void shutdown() {
		super.shutdown();
	}
	
	@Override
	public void task() throws Exception {
		setChannelFuture(serverBootstrap.bind(getServer().getHost(),
				getServer().getServerPort()).sync());
		getChannelFuture().channel().closeFuture().sync();
	}

}
