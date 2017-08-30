package org.bdc.dcm.server;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.Server;
import org.bdc.dcm.vo.e.DataType;
import org.bdc.dcm.vo.e.ServerType;
import org.junit.Test;

public class ServerManagerTest {

	/**
	 * 复写 hascode的重要性
	 */
	@Test
	public void ServerInMapKey() {
		int clientPort = 80;
		DataType dataType = DataType.Gb_dlt645_2007;
		int delaySendingTime = 0;
		Set<String> filterMacs = new HashSet<>(Arrays.asList(new String[]{"0000000000000000"}));
		String host = "192.168.0.63";
		String initSendingData= "22 22 99 77 44";
		int keepAlive = 0;
		String name = "tcp";
		String path = "aaaa";
		Map<String,String> sdkUserInfo = new HashMap<>();
		String selfMac = "";
		int serverPort = 9991;
		ServerType serverType = ServerType.HTTP_CLIENT;
		
		
		Map<Server, NettyBoot> nettyBootMap = new HashMap<>();
		
		Server server = new Server();
		server.setClientPort(clientPort);
		server.setDataType(dataType);
		server.setDelaySendingTime(delaySendingTime);
		server.setFilterMacs(filterMacs);
		server.setHost(host);
		server.setInitSendingData(initSendingData);
		server.setKeepAlive(keepAlive);
		server.setName(name);
		server.setPath(path);
		server.setSdkUserInfo(sdkUserInfo);
		server.setSelfMac(selfMac);
		server.setServerPort(serverPort);
		server.setServerType(serverType);
		NettyBoot nettyBoot = new NettyBoot(null) {
			
			@Override
			public void task() throws Exception {
				
			}
		};
		nettyBootMap.put(server, nettyBoot);
		
		Server server1 = new Server();
		server1.setClientPort(clientPort);
		server1.setDataType(dataType);
		server1.setDelaySendingTime(delaySendingTime);
		server1.setFilterMacs(filterMacs);
		server1.setHost(host);
		server1.setInitSendingData(initSendingData);
		server1.setKeepAlive(keepAlive);
		server1.setName(name);
		server1.setPath(path);
		server1.setSdkUserInfo(sdkUserInfo);
		server1.setSelfMac(selfMac);
		server1.setServerPort(serverPort);
		server1.setServerType(serverType);
		
		assertTrue(nettyBootMap.containsKey(server1));
		assertEquals(nettyBoot, nettyBootMap.get(server1));
	}

	
	@Test
	public void test(){
		class Persion implements Serializable{
			/**
			 * 
			 */
			private static final long serialVersionUID = -1873298311914739171L;
			public String name;

			public Persion(String name) {
				super();
				this.name = name;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((name == null) ? 0 : name.hashCode());
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
				Persion other = (Persion) obj;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				return true;
			}
			
		}
		Map<Persion,String> map = new HashMap<>();
		Persion p = new Persion("111");
		map.put(p, "1111");
		Persion p1 = new Persion("111");
		assertNotNull(map.get(p1));
		
	}
}
