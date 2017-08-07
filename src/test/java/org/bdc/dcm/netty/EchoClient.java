package org.bdc.dcm.netty;

import java.util.concurrent.atomic.AtomicInteger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class EchoClient {
	static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "10"));
    static AtomicInteger curOnline = new AtomicInteger(0);
    
    public static void main(String[] args) throws Exception {
    	for(int i=0;i<500;i++) {
	         new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						client();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
	        if(i==0)
	        	 Thread.sleep(1000);
    	}
    }

	private static void client() throws InterruptedException {
		// Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                    
                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     p.addLast(new EchoClientHandler());
                 }
             });

            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).addListener(new GenericFutureListener<Future<Void>>() {

				@Override
				public void operationComplete(Future<Void> future) throws Exception {
					System.out.println("成功:"+curOnline.getAndIncrement());
				}
			}).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
	}
}
