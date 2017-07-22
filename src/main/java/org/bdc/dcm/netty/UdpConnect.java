package org.bdc.dcm.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bdc.dcm.netty.channel.AbstractChannelInitializer;
import org.bdc.dcm.vo.Server;

public class UdpConnect extends NettyBoot {

	private Bootstrap bootstrap;

	public UdpConnect(Server server) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException,
			InvocationTargetException {
		super(server);
		init();
	}

	@SuppressWarnings("unchecked")
	private void init() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		setWorkerGroup(new NioEventLoopGroup());
		bootstrap = new Bootstrap();
		bootstrap
				.group(getWorkerGroup())
				.channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024);
		String classUri = "org.bdc.dcm.netty.channel.udp.Udp" + getServer().getDataType() + "ChannelInitializer";
		Class<?>[] parameter = new Class[] { String.class, Integer.class };
        invalidHost();
		Object[] args = new Object[] { getServer().getHost(),
				getServer().getClientPort() };
		AbstractChannelInitializer<DatagramChannel> channelInitializer = (AbstractChannelInitializer<DatagramChannel>) (Class
				.forName(classUri).newInstance());
		channelInitializer.setNettyBoot(this);
		Constructor<? extends AbstractChannelInitializer<DatagramChannel>> con = null;
		con = (Constructor<? extends AbstractChannelInitializer<DatagramChannel>>) channelInitializer
				.getClass().getConstructor(parameter);
		bootstrap.handler(con.newInstance(args));
	}

	@Override
	public void shutdown() {
		super.shutdown();
	}

	@Override
	public void task() throws Exception {
		// 客户端时 serverPort 可为0，即本地随机分配端口
		setChannelFuture(bootstrap.bind(getServer().getServerPort()).sync());
		getChannelFuture().channel().closeFuture().await();
	}

}
