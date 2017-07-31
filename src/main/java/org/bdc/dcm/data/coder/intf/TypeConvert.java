package org.bdc.dcm.data.coder.intf;

import io.netty.buffer.ByteBuf;

public interface TypeConvert {

	public Object decode(String type, ByteBuf value) ;
}
