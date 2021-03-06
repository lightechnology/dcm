package org.bdc.dcm.data.coder;

import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
public class LcmdbJsnEncoder implements DataEncoder<String> {

	final static Logger logger = LoggerFactory.getLogger(LcmdbJsnEncoder.class);

	private NettyBoot nettyBoot;

	public LcmdbJsnEncoder(NettyBoot nettyBoot) {
		this.nettyBoot = nettyBoot;
	}
	/**
	 * paramterStr keys :[tk,json,token,verifyCode]
	 */
	@Override
	public String package2Data(ChannelHandlerContext ctx, DataPack msg) {
		return "OK";
	}

}
