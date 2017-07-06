package org.bdc.dcm.vo;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.bdc.dcm.vo.e.DataType;
import org.bdc.dcm.vo.e.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class Server implements Serializable {

    final static Logger logger = LoggerFactory.getLogger(Server.class);

	private static final long serialVersionUID = 519325759394378139L;

	private String name = null;
	private String host = null;
	private Integer serverPort = 0;
	private Integer clientPort = 0;
	private ServerType serverType = null;
	private DataType dataType = null;
	private String selfMac = null;
	private String initSendingData = null;
	private Integer loopInitSendingDataInterval = 0;
	private Integer delaySendingTime = 0;
	private Set<String> filterMacs = null;
	private Integer keepAlive = 0;
	private String path = null;
	private Map<String, String> sdkUserInfo = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	public Integer getClientPort() {
		return clientPort;
	}

	public void setClientPort(Integer clientPort) {
		this.clientPort = clientPort;
	}

	public ServerType getServerType() {
		return serverType;
	}

	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public String getSelfMac() {
		return selfMac;
	}

	public void setSelfMac(String selfMac) {
		this.selfMac = selfMac;
	}

	public String getInitSendingData() {
		return initSendingData;
	}

	public void setInitSendingData(String initSendingData) {
		this.initSendingData = initSendingData;
	}

	public Integer getLoopInitSendingDataInterval() {
		return loopInitSendingDataInterval;
	}

	public void setLoopInitSendingDataInterval(Integer loopInitSendingDataInterval) {
		this.loopInitSendingDataInterval = loopInitSendingDataInterval;
	}

	public Integer getDelaySendingTime() {
		return delaySendingTime;
	}

	public void setDelaySendingTime(Integer delaySendingTime) {
		this.delaySendingTime = delaySendingTime;
	}
	
	public Set<String> getFilterMacs() {
		return filterMacs;
	}

	public void setFilterMacs(Set<String> filterMacs) {
		this.filterMacs = filterMacs;
	}

	public Integer getKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(Integer keepAlive) {
		this.keepAlive = keepAlive;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, String> getSdkUserInfo() {
		return sdkUserInfo;
	}

	public void setSdkUserInfo(Map<String, String> sdkUserInfo) {
		this.sdkUserInfo = sdkUserInfo;
	}

	@Override
	public boolean equals(Object obj) {
		int thisCrc = Public.bytes2Int(Public.crc16(Public.serializer(this)));
		int thatCrc = Public.bytes2Int(Public.crc16(Public.serializer(obj)));
		if (thisCrc == thatCrc)
			return true;
		return false;
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
