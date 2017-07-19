package org.bdc.dcm.comm;

import java.util.Map;
import java.util.function.Function;

import org.bdc.dcm.vo.FunIndentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LqmdbTypeConvert extends CommTypeConvert{

	private final static LqmdbTypeConvert convert = new LqmdbTypeConvert();
	
	private static Logger logger = LoggerFactory.getLogger(LqmdbTypeConvert.class);

	@Override
	public Map<FunIndentity, Function<byte[], byte[]>> getTypeToBackConvert() {
		// TODO Auto-generated method stub
		return null;
	} 
	
	
}
