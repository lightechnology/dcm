package org.bdc.dcm.vo;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Map;

import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.Datalevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class DataPack implements Serializable {

    final static Logger logger = LoggerFactory.getLogger(DataPack.class);

    private static final long serialVersionUID = -3320991840847889542L;

	// 在线状态
	private int onlineStatus;
	// 数据包类型
	private DataPackType dataPackType;
	// 设备网络地址
	private String addr;
	// 设备物理地址
	private String mac;
	// 发送目标网络地址
	private String toAddr;
	// 发送目标物理地址
	private String toMac;
	// 设备网络父节点物理地址
	private String parentMac;
	// 设备类型
	private int deviceType;
	// 数据包名
	private String dataPackName;
	// 数据回信标
	private String backSign;
	// 解析后数据
	private Map<String, Object> data;
	// 上下有变化的数据
	private Map<String, Object> differentData;
	// 数据源接口信息
	private SocketAddress socketAddress;
	// 数据有效标识
	private Datalevel datalevel;
	// 存时间戳待比较数据转换耗时
	private Long timestamp;

	public DataPack() {
		onlineStatus = -1;
		dataPackType = null;
		addr = "";
		mac = "";
		toAddr = "";
		toMac = "";
		parentMac = "";
		deviceType = -1;
		dataPackName = "";
		backSign = "";
		data = null;
		differentData = null;
		socketAddress = null;
		datalevel = Datalevel.NORMAL;
		timestamp = 0L;
	}

	public int getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(int onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public DataPackType getDataPackType() {
		return dataPackType;
	}

	public void setDataPackType(DataPackType dataPackType) {
		this.dataPackType = dataPackType;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getToAddr() {
		return toAddr;
	}

	public void setToAddr(String toAddr) {
		this.toAddr = toAddr;
	}

	public String getToMac() {
		return toMac;
	}

	public void setToMac(String toMac) {
		this.toMac = toMac;
	}

	public String getParentMac() {
		return parentMac;
	}

	public void setParentMac(String parentMac) {
		this.parentMac = parentMac;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public String getDataPackName() {
		return dataPackName;
	}

	public void setDataPackName(String dataPackName) {
		this.dataPackName = dataPackName;
	}

	public String getBackSign() {
		return backSign;
	}

	public void setBackSign(String backSign) {
		this.backSign = backSign;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public Map<String, Object> getDifferentData() {
		return differentData;
	}

	public void setDifferentData(Map<String, Object> differentData) {
		this.differentData = differentData;
	}

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	public Datalevel getDatalevel() {
		return datalevel;
	}

	public void setDatalevel(Datalevel datalevel) {
		this.datalevel = datalevel;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		try {
			return this.socketAddress.toString() + "---" + Public.map2JsonStr(data);
		} catch (Exception e) {
			if (logger.isErrorEnabled())
				logger.error(e.getMessage(), e);
			else
				e.printStackTrace();
		}
		return null;
	}
}
