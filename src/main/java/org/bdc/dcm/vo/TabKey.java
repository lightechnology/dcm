package org.bdc.dcm.vo;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class TabKey implements Serializable {

    final static Logger logger = LoggerFactory.getLogger(TabKey.class);

    private static final long serialVersionUID = -3839565808429500666L;

	private int id;
	private String name;
	private String form;
	private String units;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
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
