package org.bdc.dcm.lc;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

class ClientThread implements Runnable{

	public static final String serverInitPack = "fe a5 00 01 0c 0d";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public String mac;
	public String ip;
	public int port;
	public ClientThread(String ip,int port ,String mac) {
		super();
		this.ip = ip;
		this.port = port;
		this.mac = mac;
	}

	@Override
	public void run() {
		client();		
	}
	public void client() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup);// (2)
            b.channel(NioSocketChannel.class) // (3)
            	.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120*1000)
	        	.option(ChannelOption.SO_KEEPALIVE, true)
	            .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
	            .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                	ChannelPipeline  pipline = ch.pipeline();
                	//pipline.addLast(new LoggingHandler(LogLevel.ERROR));
                	pipline.addLast(new MessageToMessageDecoder<ByteBuf>() {

						@Override
						protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
								throws Exception {
							if(msg.isReadable()) {
								ByteBuf frame = msg.alloc().buffer();
								byte[] bs = new byte[msg.readableBytes()];
								msg.readBytes(bs);
								frame.writeBytes(bs);
								out.add(frame);
							}
						}
					});
                	pipline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {

						@Override
						public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
								throws Exception {
							super.write(ctx, msg, promise);
							
						}

						@Override
						protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
							int len = msg.readableBytes();
							byte[] bs = new byte[len];
							msg.readBytes(bs);
							ByteBuf out = ctx.alloc().buffer();
							if(Public.byte2hex(bs).equalsIgnoreCase(serverInitPack)) {//回复mac
								byte[] macBs = Public.hexString2bytes(DataGernator.writeMacPack(mac));
								out.writeBytes(macBs);
								ctx.writeAndFlush(out).addListener(new GenericFutureListener<Future<Void>>() {

									@Override
									public void operationComplete(Future<Void> future) throws Exception {
										//logger.error("客户端发送成功~Mac:{}",ctx.channel().id());
									}
								});
								
							}else {//检查是询问那个mac地
								
								int b = msg.getByte(14)&0xff;
								String pack = DataGernator.writePackInfo(mac, b<10?"0"+b:b+"");
								byte[] macBs = Public.hexString2bytes(pack);
								out.writeBytes(macBs);
								ctx.writeAndFlush(out).addListener(new GenericFutureListener<Future<Void>>() {

									@Override
									public void operationComplete(Future<Void> future) throws Exception {
										//logger.error("客户端发送成功~信息:{}",ctx.channel().id());
									}
								});
								
							}
							
						}
                    	
					});
                }
            });
            
            // Start the client.
            ChannelFuture f = b.connect(ip, port).addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					MultiClientTest.successNum.getAndIncrement();
					System.err.println("客户端连接成功数："+MultiClientTest.successNum.get());
				}
			}).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        }catch(Exception e) {
        	e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
	}
}
public class MultiClientTest {

	public static AtomicInteger successNum = new AtomicInteger(0);
	@Test
	public  void startUp() {
		for(int i=0,j=60;i<700;i++,j++) {
			new Thread(new ClientThread("192.168.0.63",6004,"00 12 4B 00 "+Public.byte2hex(Public.int2Bytes(j, 4)))).start();
		}
		try {
			Thread.sleep(1000*20*60);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public  void startOneUp() {
		new ClientThread("192.168.0.63",6004,"00 12 4B 00 0A DC 89 50").run();
		
	}
}
