package org.bdc.dcm.vo;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class DataTab implements Serializable {

    final static Logger logger = LoggerFactory.getLogger(DataTab.class);

    private static final long serialVersionUID = -8394078918462411671L;

	private int id;
	private String name;
	private String oname;
	private String form;
	private String units;
	private int kind;

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

	public String getOname() {
		return oname;
	}

	public void setOname(String oname) {
		this.oname = oname;
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

	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
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
