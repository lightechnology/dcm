package org.bdc.dcm.netty.coder.tcp;

import org.bdc.dcm.data.coder.Gb_dlt645_2007Encoder;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.data.coder.tcp.DataTcpEncoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;

import io.netty.buffer.ByteBuf;

public class TcpGb_dlt645_2007Encoder  extends TcpEncoder  {

	public TcpGb_dlt645_2007Encoder(Logger logger, NettyBoot nettyBoot, DataEncoder<ByteBuf> encoder) {
		super(logger, nettyBoot, new DataTcpEncoder<ByteBuf>(new Gb_dlt645_2007Encoder()));
	}

}
