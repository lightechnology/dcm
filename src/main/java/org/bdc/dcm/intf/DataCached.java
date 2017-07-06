package org.bdc.dcm.intf;

import org.bdc.dcm.vo.KeyTable;


public interface DataCached<T> {
	
	public void offer(T value);
	
	public void offer(T value, int expire);
	
	public T peek(String key);
	
	public KeyTable keyTable();

}
