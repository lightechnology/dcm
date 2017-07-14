package org.bdc.dcm.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LqTypeConvert extends CommTypeConvert{

	private final static LqTypeConvert convert = new LqTypeConvert();
	
	private static Logger logger = LoggerFactory.getLogger(LqTypeConvert.class); 
	
	@Override
	protected void initTypeTokey() throws Exception{
		
		for(int i=0;i<13;i++){
			typeToBackConvert.put(i, numberToBoolean);
		}
	}	

}
