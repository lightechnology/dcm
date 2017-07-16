package org.bdc.dcm.netty.framer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LcmdbFrameDecoder extends ByteToMessageDecoder {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
            List<Object> out) throws Exception {
        // 有足够数据时
        while (in.isReadable() && 1 < in.readableBytes()) {
            // 标记当前位置，以便reset
            in.markReaderIndex();
            // 判断是否是FE A5开头
            if ((byte) 0xFE == in.readByte() && (byte) 0xA5 == in.readByte()) {
            	byte type = in.readByte();
            	int packLen = in.readByte() & 0xff;
            	byte[] data = new byte[packLen];
            	in.readBytes(data);
            	byte crcSum = in.readByte();
            	//不包涵两个头部信息
            	int sum = (type&0xff) + packLen;
            	
            	for(int i=0;i<data.length;i++){
            		sum+=(data[i] & 0xff);
            	}
            	byte calcSum = (byte)(sum & 0xff);
            	if(crcSum == calcSum){
            		in.resetReaderIndex();
            		//两字节头 +1 字节类型 + 1字节长度 +1 字节校验和
            		ByteBuf frame = in.readBytes(4+packLen+1);
            		byte[] frameBytes = new byte[frame.readableBytes()];
            		frame.markReaderIndex();
            		frame.readBytes(frameBytes);
            		//防止单独设置时 返回的数据
        			//if(packLen == 45 || packLen == 9){//只解轮训状态的包
	            		frame.resetReaderIndex();
	            		out.add(frame);
        			//}
            	}  
            }else{
            	in.resetReaderIndex();
            	in.readableBytes();
            }
        }
    }

}
