package org.bdc.dcm.server;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.intf.ServerConf;
import org.bdc.dcm.netty.HttpClient;
import org.bdc.dcm.netty.HttpServer;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.TcpClient;
import org.bdc.dcm.netty.TcpServer;
import org.bdc.dcm.netty.UdpConnect;
import org.bdc.dcm.vo.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.ComParam;
import com.util.tools.Public;

/**
 * 通过服务配置信息接口获取最新服务信息 管理启动中的服务列表，即可实时更新最新服务配置下的数据接口服务
 * 
 * @author adolp
 *
 */
public class ServerManager implements Runnable {

	final static Logger logger = LoggerFactory.getLogger(ServerManager.class);

	private ServerConf serverConf;
	private Map<Server, NettyBoot> nettyBootMap;
	private Thread thread;
	private AtomicBoolean run;

	private List<Server> lastTimeServerConfList;

	public ServerManager() {
		this.serverConf = IntfConf.getServerConf();
		this.nettyBootMap = new HashMap<Server, NettyBoot>();
		this.thread = new Thread(this);
		this.lastTimeServerConfList = null;
		this.run = new AtomicBoolean(false);
		startup();
	}

	public void startup() {
		run.set(true);
		thread.start();
	}

	/*
	 * 停了所有服务后才能停止这个线程
	 */
	public void shutdown() {
		run.set(false);
	}

	@Override
	public void run() {
		while (run.get()) {
			List<Server> removeServerConfList = new ArrayList<Server>();
			List<Server> addServerConfList = new ArrayList<Server>();
			getServerConfList(removeServerConfList, addServerConfList);
			removeServer(removeServerConfList);
			addServer(addServerConfList);
			Public.sleepWithOutInterrupted(ComParam.ONEMIN);
		}
		// 停止所有活跃服务!!!!!!!!!!!!!!!!!!!!!!
		if (null != lastTimeServerConfList)
		    removeServer(lastTimeServerConfList);
	}
	
	/**
	 * 添加服务
	 * 
	 * @param serverConfList
	 */
	public void addServer(List<Server> serverConfList) {
		for (int i = 0; i < serverConfList.size(); i++) {
			addServer(serverConfList.get(i));
		}
	}
	
	/**
	 * 添加服务
	 * 
	 * @param server
	 */
	public void addServer(Server server) {
		NettyBoot nettyBoot = null;
        try {
            switch (server.getServerType()) {
            case TCP_CLIENT:
                nettyBoot = new TcpClient(server);
                break;
            case TCP_SERVER:
                nettyBoot = new TcpServer(server);
                break;
            case UDP_CONNECT:
                nettyBoot = new UdpConnect(server);
                break;
            case HTTP_CLIENT:
                nettyBoot = new HttpClient(server);
                break;
            case HTTP_SERVER:
                nettyBoot = new HttpServer(server);
                break;
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | SecurityException
                | IllegalArgumentException | NoSuchMethodException
                | InvocationTargetException e) {
            if (logger.isErrorEnabled())
                logger.error(e.getMessage(), e);
            else
                e.printStackTrace();
            // 初始出错的服务，尝试下次轮询检测启动
            lastTimeServerConfList.remove(server);
            return;
        }
        if (null != nettyBoot) {
            nettyBootMap.put(server, nettyBoot);
            if (logger.isInfoEnabled())
                logger.info("startup: " + server);
            nettyBoot.startup();
        }
	}
	
	/**
	 * 删除服务
	 * 
	 * @param serverConfList
	 */
	public void removeServer(List<Server> serverConfList) {
		for (int i = 0; i < serverConfList.size(); i++) {
			removeServer(serverConfList.get(i));
		}
	}
	
	/**
	 * 删除服务
	 * 
	 * @param server
	 */
	public void removeServer(Server server) {
		//通过server即可定位服务对象进行停止并消除，当然这个类必须能管理创建的服务对象 TODO 这里 get不到nettyBoot
		logger.warn("存在吗？：{}",nettyBootMap.containsKey(server));
		NettyBoot nettyBoot = nettyBootMap.get(server);
		if (null != nettyBoot) {
			nettyBootMap.remove(server);
			if (logger.isInfoEnabled())
				logger.info("shutdown: " + server);
			nettyBoot.shutdown();
		}
	}

	/**
	 * 比较上下文服务配置信息，获取那些服务新增，那些要删除
	 * 
	 * @param removeServerConfList
	 *            删除服务配置列表
	 * @param addServerConfList
	 *            新增服务配置列表
	 */
	public void getServerConfList(List<Server> removeServerConfList,
			List<Server> addServerConfList) {
		assert (null != addServerConfList && null != removeServerConfList);
		addServerConfList.clear();
		removeServerConfList.clear();
		List<Server> serverConfList = serverConf.newestServerList();
		if (null == serverConfList) return;
		if (null != lastTimeServerConfList) {
			//上一次服务配置 与 本次服务配置 比较 上一次服务配置 不在本次服务配置的 归类于 删除服务项
			equalsServerList(lastTimeServerConfList, serverConfList, removeServerConfList);
			//本次服务配置 与 本次服务配置 比较 本次服务配置 不在上一次次服务配置的 归类于 增加项
			equalsServerList(serverConfList, lastTimeServerConfList, addServerConfList);
		} else
			addServerConfList.addAll(serverConfList);
		lastTimeServerConfList = serverConfList;
	}
	
	protected void equalsServerList(List<Server> serverConfList1,
			List<Server> serverConfList2, List<Server> serverConfList3) {
		for (int i = 0; i < serverConfList1.size(); i++) {
			boolean has = false;
			Server server = serverConfList1.get(i);
			for (int j = 0; j < serverConfList2.size(); j++) {
				Server lastTimeServer = serverConfList2.get(j);
				if (!server.equals(lastTimeServer))
					continue;
				has = true;
				break;
			}
			if (!has) {
				serverConfList3.add(server);
			}
		}
	}

}
