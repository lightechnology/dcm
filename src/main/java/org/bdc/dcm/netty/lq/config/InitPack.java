package org.bdc.dcm.netty.lq.config;

import java.util.Arrays;

public class InitPack {

	private String mac;
	
	private byte[] deviceAddrs ;

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public byte[] getDeviceAddrs() {
		return deviceAddrs;
	}

	public void setDeviceAddrs(byte[] deviceAddrs) {
		this.deviceAddrs = deviceAddrs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mac == null) ? 0 : mac.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InitPack other = (InitPack) obj;
		if (mac == null) {
			if (other.mac != null)
				return false;
		} else if (!mac.equals(other.mac))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LqInitPack [mac=" + mac + ", deviceAddrs=" + Arrays.toString(deviceAddrs) + "]";
	}

	
	
	
}
