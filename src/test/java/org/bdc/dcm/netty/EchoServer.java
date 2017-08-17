package org.bdc.dcm.netty;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
/**
 * Echoes back any received data from a client.
 */
public final class EchoServer {

	public static Integer bossNEventLoops; 
	public static Integer workNEventLoops;
	
	public static Logger logger = LoggerFactory.getLogger(EchoServer.class);
	
	static{
		try {
			Properties properties = new Properties();
			InputStream in = EchoServer.class.getClassLoader().getResourceAsStream("org/bdc/dcm/netty/properties/EchoServer.properties");
			properties.load(in);
			bossNEventLoops = Integer.valueOf((String) properties.get("bossGroup.nEventLoops"));
			workNEventLoops = Integer.valueOf((String) properties.get("workerGroup.nEventLoops"));
			in.close();
			logger.error("properties:{}",properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));

    public static void main(String[] args) throws Exception {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(EchoServer.bossNEventLoops);
        EventLoopGroup workerGroup = new NioEventLoopGroup(EchoServer.workNEventLoops);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
	            .channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 128)
	            .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 256 * 1024)
	            .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
	            .childOption(ChannelOption.SO_KEEPALIVE, true)
	            .childHandler(new TestChannelInitializer());

            // Start the server.
            ChannelFuture f = b.bind(PORT).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}