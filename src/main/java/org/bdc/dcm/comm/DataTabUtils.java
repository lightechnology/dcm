package org.bdc.dcm.comm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataType;

import com.util.tools.Public;

public class DataTabUtils {

	public static List<DataTab> initDataTabConf(DataType dataType) throws Exception {
		List<DataTab> dataTabConf = new ArrayList<>();
		System.out.println(dataType.name());
		InputStream in = DataTabUtils.class.getResourceAsStream(dataType.name()+"Pack.json");
		Map<String, Object> dataTabConfMap = Public.str2Map(Public.inputStream2String(in));
        assert (null != dataTabConfMap);
        dataTabConf.clear();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dataTabList = (List<Map<String, Object>>) dataTabConfMap
                .get("dataTabConf");
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
        in.close();
        return dataTabConf;
    }
}
