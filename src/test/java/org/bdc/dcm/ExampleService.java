package org.bdc.dcm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.intf.ServerConf;
import org.bdc.dcm.server.ServerManager;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.Server;
import org.bdc.dcm.vo.e.DataType;
import org.bdc.dcm.vo.e.ServerType;

import com.util.tools.Public;

import freemarker.template.TemplateException;

public class ExampleService implements DataTabConf, ServerConf {

	private static final String serverConfUri = "org/bdc/dcm/serverConf.json";
	private static final String dataTabConfUri = "org/bdc/dcm/dataTabConf.json";

	private List<Server> serverConf;
	private List<DataTab> dataTabConf;

	private static final ExampleService exampleService = new ExampleService();

	private ExampleService() {
		this.serverConf = new ArrayList<Server>();
		this.dataTabConf = new ArrayList<DataTab>();
		init();
	}

	public static ExampleService getInstance() {
		return exampleService;
	}

	public void init() {
		initServerConf();
		initDataTabConf();
	}
	
	/**
	 * 得到最新的服务配置
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Server> newestServerList() {
		Map<String, Object> serverConfMap = getDataServerConfMap(
				getClass().getClassLoader().getResourceAsStream(serverConfUri));
		List<Server> serverConf = new ArrayList<>();

		List<Map<String, Object>> serverList = (List<Map<String, Object>>) serverConfMap.get("serverConf");
		for (int i = 0; i < serverList.size(); i++) {
			Map<String, Object> serverMap = serverList.get(i);
			if (0 == Public.objToInt(serverMap.get("dataLevel"))) {
				serverConf.add(mapToServer(serverMap));
			}
		}
		return serverConf;
	}
	
	@SuppressWarnings("unchecked")
	public void initServerConf() {
		Map<String, Object> serverConfMap = getDataServerConfMap(
				getClass().getClassLoader().getResourceAsStream(serverConfUri));
		assert (null != serverConfMap);
		serverConf.clear();
		List<Map<String, Object>> serverList = (List<Map<String, Object>>) serverConfMap.get("serverConf");
		for (int i = 0; i < serverList.size(); i++) {
			Map<String, Object> serverMap = serverList.get(i);
			if (0 == Public.objToInt(serverMap.get("dataLevel"))) {
				serverConf.add(mapToServer(serverMap));
			}
		}
	}

	public void initDataTabConf() {
		Map<String, Object> dataTabConfMap = getDataTabConfMap(
				getClass().getClassLoader().getResourceAsStream(dataTabConfUri));
		assert (null != dataTabConfMap);
		dataTabConf.clear();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> dataTabList = (List<Map<String, Object>>) dataTabConfMap.get("dataTabConf");
		for (int i = 0; i < dataTabList.size(); i++) {
			Map<String, Object> dataTabMap = dataTabList.get(i);
			if (0 == Public.objToInt(dataTabMap.get("dataLevel"))) {
				DataTab dataTab = new DataTab();
				dataTab.setId(Public.objToInt(dataTabMap.get("id")));
				dataTab.setName(Public.objToStr(dataTabMap.get("name")));
				dataTab.setForm(Public.objToStr(dataTabMap.get("form")));
				dataTab.setUnits(Public.objToStr(dataTabMap.get("units")));
				dataTabConf.add(dataTab);
			}
		}
	}

	public Map<String, Object> getDataServerConfMap(InputStream dataServerConf) {
		try {
			return Public.str2Map(Public.inputStream2String(dataServerConf));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Object> getDataTabConfMap(InputStream dataTabConf) {
		try {
			return Public.str2Map(Public.inputStream2String(dataTabConf));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Server> getServerConf() {
		// initServerConf();
		return serverConf;
	}

	@Override
	public List<DataTab> getDataTabConf(String name) {
		// initDataTabConf();
		return dataTabConf;
	}

	public static void main(String[] args) throws IOException, TemplateException {
		IntfConf.setDataTabConf(ExampleService.getInstance());
		IntfConf.setServerConf(ExampleService.getInstance());
		new Thread(new ServerManager()).start();
	}
	/**
	 * 统一 map 转 Server 
	 * @param serverMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Server mapToServer(Map<String, Object> serverMap) {
		Server server = new Server();
		server.setName(Public.objToStr(serverMap.get("name")));
		server.setServerType(ServerType.values()[Public.objToInt(serverMap.get("serverType"))]);
		server.setDataType(DataType.values()[Public.objToInt(serverMap.get("dataType"))]);
		server.setHost(Public.objToStr(serverMap.get("host")));
		server.setServerPort(Public.objToInt(serverMap.get("serverPort")));
		server.setClientPort(Public.objToInt(serverMap.get("clientPort")));
		server.setInitSendingData(Public.objToStr(serverMap.get("initSendingData")));
		server.setDelaySendingTime(Public.objToInt(serverMap.get("delaySendingTime")));
		server.setSelfMac(Public.objToStr(serverMap.get("selfMac")));
		List<String> l = (List<String>) serverMap.get("filterMacs");
		Set<String> filterMacs = new HashSet<String>();
		for (int ii = 0; ii < l.size(); ii++) {
			filterMacs.add(l.get(ii));
		}
		server.setFilterMacs(filterMacs);
		return server;
	}
}
