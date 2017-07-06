package org.bdc.dcm.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class KeyTable implements Serializable {

    final static Logger logger = LoggerFactory.getLogger(KeyTable.class);

	private static final long serialVersionUID = -5026133553890664381L;

	private String tableKey;
	private Map<String, Integer> keyValue;

	public KeyTable(String tableKey) {
		this.tableKey = tableKey;
		this.keyValue = new HashMap<String, Integer>();
	}

	public Logger getLogger() {
		return logger;
	}

	public String getTableKey() {
		return tableKey;
	}

	public void setTableKey(String tableKey) {
		this.tableKey = tableKey;
	}

	public Map<String, Integer> getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(Map<String, Integer> keyValue) {
		this.keyValue = keyValue;
	}

	@Override
	public String toString() {
		try {
			return Public.map2JsonStr(Public.vo2Map(this));
		} catch (Exception e) {
			if (logger.isErrorEnabled())
				logger.error(e.getMessage(), e);
			else
				e.printStackTrace();
		}
		return null;
	}
}
