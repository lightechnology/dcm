package org.bdc.dcm.data.coder;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.Server;

public class XarjsnDecoder implements DataDecoder<String> {
	
	private NettyBoot nettyBoot;
	
	public XarjsnDecoder(NettyBoot nettyBoot) {
		this.nettyBoot = nettyBoot;
	}

	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, String msg) {
		Server server = nettyBoot.getServer();
		Map<String, String> sdkInfo = server.getSdkUserInfo();
		return null;
	}
	
}
