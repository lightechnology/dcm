package org.bdc.dcm.vo;

public class ConfModel extends DataTab {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2624752988453124555L;

	private int validate ;
	
	public ConfModel(String form){
		setForm(form);
	}

	public int getValidate() {
		return validate;
	}

	public void setValidate(int validate) {
		this.validate = validate;
	}

	
	
}
