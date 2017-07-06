package org.bdc.dcm.netty.coder.tcp;

import org.bdc.dcm.data.coder.KlvDecoder;
import org.bdc.dcm.data.coder.tcp.DataTcpDecoder;
import org.bdc.dcm.netty.NettyBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

public class TcpKlvDecoder extends TcpDecoder {

    final static Logger logger = LoggerFactory.getLogger(TcpKlvDecoder.class);

    public TcpKlvDecoder(NettyBoot nettyBoot) {
        super(logger, nettyBoot, new DataTcpDecoder<ByteBuf>(new KlvDecoder()));
    }


}
