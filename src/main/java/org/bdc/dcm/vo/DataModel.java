package org.bdc.dcm.vo;

public class DataModel extends DataTab{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4711569052577536538L;

	private Object val;

	public Object getVal() {
		return val;
	}

	public void setVal(Object val) {
		this.val = val;
	}

	public DataModel() {
		super();
	}

	public DataModel(Object val,String form) {
		super();
		this.val = val;
		setForm(form);
	}

	public static DataModel mapBy(DataTab tab,Object val){
		DataModel dataModel = new DataModel(val,tab.getForm());
		dataModel.setId(tab.getId());
		dataModel.setKind(tab.getKind());
		dataModel.setName(tab.getName());
		dataModel.setOname(tab.getOname());
		dataModel.setUnits(tab.getUnits());
		return dataModel;
	}
	
}
