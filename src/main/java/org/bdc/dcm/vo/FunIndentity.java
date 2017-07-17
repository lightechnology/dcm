package org.bdc.dcm.vo;

import org.bdc.dcm.vo.e.DataType;

public class FunIndentity {

	private DataType type;
	
	private Integer id;

	
	public FunIndentity(DataType type, Integer id) {
		super();
		this.type = type;
		this.id = id;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
