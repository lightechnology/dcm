package org.bdc.dcm.lc;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

class ClientThread implements Runnable{

	public static final String serverInitPack = "fe a5 00 01 0c 0d";
	
	
	
	
	public String mac;
	public int addr;
	public String ip;
	public int port;
	public ClientThread(String ip,int port ,String mac, int addr) {
		super();
		this.ip = ip;
		this.port = port;
		this.mac = mac;
		this.addr = addr;
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
	        	.option(ChannelOption.SO_KEEPALIVE, true)
	            .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
	            .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                	ChannelPipeline  pipline = ch.pipeline();
                	pipline.addLast(new LoggingHandler(LogLevel.INFO));
                	pipline.addLast(new MessageToMessageDecoder<ByteBuf>() {

						@Override
						protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
								throws Exception {
							if(msg.isReadable()) {
								ByteBuf frame = msg.alloc().buffer();
								frame.writeBytes(msg);
								out.add(frame);
							}
						}
					});
                	pipline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                    	private boolean first = false;
                    	private Logger logger = LoggerFactory.getLogger(this.getClass());

						@Override
						protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
							int len = msg.readableBytes();
							byte[] bs = new byte[len];
							msg.readBytes(bs);
							ByteBuf out = ctx.alloc().buffer();
							if(Public.byte2hex(bs).equalsIgnoreCase(serverInitPack)) {//恢复mac
								byte[] macBs = Public.hexString2bytes(DataGernator.writeMacPack(mac));
								out.writeBytes(macBs);
								ctx.writeAndFlush(out);
							}
//							ByteBuf out = ctx.alloc().buffer();
//							if(!first) {
//								first = true;
//								out.writeBytes(Public.hexString2bytes("FE A5 FF 09 0C "+mac));
//								ctx.writeAndFlush(out);
//							}else {
//								out.writeBytes(Public.hexString2bytes(DataGernator.write(mac,addr+"")));
//								ctx.writeAndFlush(out);
//							}
						}
                    	
					});
                }
            });
            
            // Start the client.
            ChannelFuture f = b.connect(ip, port).sync(); // (5)

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

	@Test
	public  void startUp() {
		for(int i=1;i<2;i++) {
			new Thread(new ClientThread("192.168.0.63",6001,"00 12 4B 00 0A DC 89 5"+i, i)).start();
		}
		while(true);
	}

	@Test
	public void equal() {
		System.out.println(1<1);
	}
}
