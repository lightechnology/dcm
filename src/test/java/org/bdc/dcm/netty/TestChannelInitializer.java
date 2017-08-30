package org.bdc.dcm.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class TestChannelInitializer extends ChannelInitializer<SocketChannel> {

     @Override
     public void initChannel(SocketChannel ch) throws Exception {
    	 ChannelPipeline piple = ch.pipeline();
    	 piple.addLast(new TestFrameDecoder());
    	 piple.addLast(new EchoServerHandler());
    	 
     }
     
}
