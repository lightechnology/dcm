package org.bdc.dcm.intf;

import java.util.List;

import org.bdc.dcm.vo.Server;

public interface ServerConf {
	
	public List<Server> getServerConf();
	
	/**
	 * 拿到最新的来自配置文件的配置
	 * @return
	 */
	public List<Server> newestServerList();
	
}
