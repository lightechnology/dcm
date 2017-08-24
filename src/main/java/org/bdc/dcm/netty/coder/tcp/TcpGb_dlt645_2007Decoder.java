package org.bdc.dcm.netty.coder.tcp;

import org.bdc.dcm.data.coder.Gb_dlt645_2007Decoder;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.coder.tcp.DataTcpDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;

import io.netty.buffer.ByteBuf;

public class TcpGb_dlt645_2007Decoder extends TcpDecoder  {

	public TcpGb_dlt645_2007Decoder(Logger logger, NettyBoot nettyBoot, DataDecoder<ByteBuf> decoder) {
		super(logger, nettyBoot, new DataTcpDecoder<ByteBuf>(new Gb_dlt645_2007Decoder()));
	}

}
