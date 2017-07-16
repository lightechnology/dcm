package org.bdc.dcm.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LqmdbTypeConvert extends CommTypeConvert{

	private final static LqmdbTypeConvert convert = new LqmdbTypeConvert();
	
	private static Logger logger = LoggerFactory.getLogger(LqmdbTypeConvert.class); 
	
	@Override
	protected void initRegTokey() throws Exception{
		
		for(int i=0;i<13;i++){
			typeToBackConvert.put(i, numberToBoolean);
		}
	}	

}
