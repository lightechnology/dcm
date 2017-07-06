package org.bdc.dcm.data.coder.intf;

import java.util.List;


public interface WriteCoder<I> {

	public void write2(I msg, List<Object> out);
	
}
