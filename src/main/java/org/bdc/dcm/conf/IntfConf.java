package org.bdc.dcm.conf;

import org.bdc.dcm.intf.DataCached;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.intf.ServerConf;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.KeyTable;

public class IntfConf {
	
	private static ServerConf serverConf;
	private static DataTabConf dataTabConf;
	private static DataCached<DataPack> dataPackCached;
	private static DataCached<KeyTable> keyTableCached;

	public static ServerConf getServerConf() {
		return serverConf;
	}

	public static void setServerConf(ServerConf serverConf) {
		IntfConf.serverConf = serverConf;
	}

	public static DataTabConf getDataTabConf() {
		return dataTabConf;
	}

	public static void setDataTabConf(DataTabConf dataTabConf) {
		IntfConf.dataTabConf = dataTabConf;
	}

	public static DataCached<DataPack> getDataPackCached() {
		return dataPackCached;
	}

	public static void setDataPackCached(DataCached<DataPack> dataPackCached) {
		IntfConf.dataPackCached = dataPackCached;
	}

	public static DataCached<KeyTable> getKeyTableCached() {
		return keyTableCached;
	}

	public static void setKeyTableCached(DataCached<KeyTable> keyTableCached) {
		IntfConf.keyTableCached = keyTableCached;
	}
	
}
