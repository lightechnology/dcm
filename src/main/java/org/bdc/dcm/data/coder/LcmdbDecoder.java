package org.bdc.dcm.data.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.coder.lcmdb.CommandTypeCtr;
import org.bdc.dcm.data.coder.lcmdb.vo.CommLcParam;
import org.bdc.dcm.vo.DataPack;

import com.util.tools.Public;


public class LcmdbDecoder implements DataDecoder<ByteBuf> {
	
	private int headerLen = 2;
	
	// 通过DataTypeConf接口获取解码规则
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, ByteBuf msg) {
		
		//----------------数据定义区-------------------------------------------
		byte[] headerBytes = new byte[headerLen];
		msg.readBytes(headerBytes);
		byte typeByte = msg.readByte();
		//捕获数据包长度 不包行crc校验和
		int packLen = (msg.readByte() & 0xff) - 1;
		
		//读包命令
		byte commandByte = msg.readByte();
		//因为 packlen 不包行crc校验和
		ByteBuf pack =msg.readBytes(packLen);
		msg.readByte();//最后一位 crc 和 保证 读完
		//----------------动态编码加载区-------------------------------------------
		String command = Public.byte2hex_ex(commandByte);
		String classPath = "org.bdc.dcm.data.coder.lcmdb.decoder.CmdDecoder_"+command+"H";
		try {
			Class<?> ctrClazz = Class.forName(classPath);
			CommandTypeCtr ctr = (CommandTypeCtr) ctrClazz.newInstance();
			DataPack datapack =  ctr.mapTo(new CommLcParam(typeByte,pack));
			return datapack;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
