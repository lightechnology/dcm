package org.bdc.dcm.jsonobject;

import static org.junit.Assert.assertEquals;

import java.util.List;

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
}
