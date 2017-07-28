package org.bdc.dcm.jsonobject;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bdc.dcm.lc.DataGernator;
import org.json.simple.JSONObject;
import org.junit.Test;

import com.util.tools.Public;


public class JsonObjectTest {

	@Test
	public void readList() {
		String json = "{\"mac\":[\"mac1\",\"mac2\"]}";
		JSONObject jsonObject = Public.str2Json(json);
		List<String> list = (List<String>) jsonObject.get("mac");
		assertEquals("mac1", list.get(0));
		assertEquals("mac2", list.get(1));
	}
	@Test
	public void gernatorLcmdDataTest() {
		for(int i=1;i<21;i++) {
			String addr = "";
			if(i<10)
				addr = "0"+i;
			else
				addr = i+"";
			System.out.println(DataGernator.writePackInfo("00 12 4B 00 0A DC 89 5B",addr));
		}
		
	} 
}
