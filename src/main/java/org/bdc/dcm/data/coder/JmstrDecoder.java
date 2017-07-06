package org.bdc.dcm.data.coder;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.Datalevel;

import com.util.tools.Public;

public class JmstrDecoder implements DataDecoder<String> {
	
	private final DataTabConf dataTabConf;

	public JmstrDecoder() {
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	
	// 通过DataTypeConf接口获取解码规则
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, String msg) {
		String[] ds = msg.split("_");
		DataPack dataPack = getInitDataPack(ds[0]);
		String kn = ds[1].substring(2, 3);
		String ln = ds[1].substring(3, 4);
		int kind = Public.objToInt(ds[1].substring(4, 5));
		Map<Integer, DataTab> dataTabMap = getDataTabConf(kind);
		if (null == dataTabMap) return null;
		Map<String, Object> data = new HashMap<String, Object>();
		for (int i = 2; i < ds.length - 1; i++) {
			int it = i - 1;
			data.put(ln + "_" + it + "_" + kn,
					makeMapValue(dataTabMap.get(it).getName(), ds[i]));
		}
		data.put("kind", kind);
		dataPack.setData(data);
		return dataPack;
	}
	
	private DataPack getInitDataPack(String mac) {
		DataPack dataPack = new DataPack();
		dataPack.setMac(mac);
		dataPack.setOnlineStatus(1);
		dataPack.setDatalevel(Datalevel.NORMAL);
		dataPack.setDataPackType(DataPackType.Info);
		return dataPack;
	}
	
	private Map<Integer, DataTab> getDataTabConf(int kind) {
		List<DataTab> dataTabs = dataTabConf.getDataTabConf("jm");
		Map<Integer, DataTab> dataTabMap = new HashMap<Integer, DataTab>();
		for (int i = 0; i < dataTabs.size(); i++) {
			DataTab dataTab = dataTabs.get(i);
			if (kind == dataTab.getKind()) {
				dataTabMap.put(dataTab.getId(), dataTab);
			}
		}
		if (dataTabMap.isEmpty())
			return null;
		return dataTabMap;
	}
	
	private List<Object> makeMapValue(String name, Object value) {
		List<Object> vl = new ArrayList<Object>();
		vl.add(name);
		vl.add(value);
		return vl;
	}
	
}
