package org.bdc.dcm.data.log.intf;

import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;

public interface Coder4Log<I> {

	public String log(I msg, DataPack dataPack);
	
	public Logger getLogger();

}
