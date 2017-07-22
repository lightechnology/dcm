package org.bdc.dcm.netty.klv;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bdc.dcm.conf.ComConf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.util.tools.Public;

public class KlvTypeConvert {
	
	private final static int DATATYPE_BYTE = 0;
	private final static int DATATYPE_INT = 1;
	private final static int DATATYPE_FLOAT = 2;
	private final static int DATATYPE_CHAR = 3;
	private final static int DATATYPE_IP = 4;
	private final static int DATATYPE_NEGATIVE_INT = 5;
	private final static int DATATYPE_RSDATA = 6;
	private final static int DATATYPE_PIODATA = 7;
	private final static int DATATYPE_PADDATA = 8;
	private final static int DATATYPE_UNICODE = 9;
	private final static int DATATYPE_INTIME = 10;
	
	private static int convertTypeStr2TypeId(String type) {
		if ("byte".equals(type)) {
			return DATATYPE_BYTE;
		}
		if ("int".equals(type)) {
			return DATATYPE_INT;
		}
		if ("float".equals(type)) {
			return DATATYPE_FLOAT;
		}
		if ("char".equals(type)) {
			return DATATYPE_CHAR;
		}
		if ("ip".equals(type)) {
			return DATATYPE_IP;
		}
		if ("-int".equals(type)) {
			return DATATYPE_NEGATIVE_INT;
		}
		if ("rsdata".equals(type)) {
			return DATATYPE_RSDATA;
		}
		if ("piodata".equals(type)) {
			return DATATYPE_PIODATA;
		}
		if ("paddata".equals(type)) {
			return DATATYPE_PADDATA;
		}
		if ("unicode".equals(type)) {
			return DATATYPE_UNICODE;
		}
		if ("intime".equals(type)) {
			return DATATYPE_INTIME;
		}
		return -1;
	}
	
	private static String bytes2FloatStr(byte[] data) {
		try {
			return Float.toString(Public.bytes2float(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private static byte[] floatStr2Bytes(String value) {
		try {
			return Public.float2Bytes(Public.objToFloat(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String bytes2String(byte[] data) {
		try {
			return new String(data, ComConf.getInstance().CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private static byte[] string2Bytes(String value) {
		try {
			return value.getBytes(ComConf.getInstance().CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String bytes2IpStr(byte[] data) {
		StringBuilder sb = new StringBuilder();
		if (0 < data.length) {
			sb.append(Public.bytes2Int(new byte[] { data[0] }));
			for (int i = 1; i < data.length; i++)
				sb.append(".").append(Public.bytes2Int(new byte[] { data[i] }));
		}
		return sb.toString();
	}
	
	private static byte[] ipStr2Bytes(String value) {
		String[] vs = value.split(".");
		byte[] data = new byte[vs.length];
		for (int i = 0; i < vs.length; i++) {
			data[i] = Public.int2Bytes(Public.objToInt(vs[i]), 1)[0];
		}
		return data;
	}
	
	private static Map<String, Object> anaRssiInfos(byte[] data) {
		Map<String, Object> deviceRssiLqiMap = new HashMap<String, Object>();
		String s[] = Public.byte2hex(data).replace(" ", "").split(" ");
		for (int si = 0; si < s.length; si++) {
			String addr = s[si] + s[si + 1];
			String mac = "";
			for (int sj = 0; sj < 8; sj++) {
				mac = mac + s[si + sj + 2];
			}
			String rssi = "-" + Public.bytes2Int(Public.hexString2bytes(s[si + 10]));
			String lqi = Integer.toString(Public.bytes2Int(Public.hexString2bytes(s[si + 11])));
			si = si + 11;
			Map<String, Object> infomap = new HashMap<String, Object>();
			infomap.put("addr", addr);
			infomap.put("rssi", rssi);
			infomap.put("lqi", lqi);
			deviceRssiLqiMap.put(mac, infomap);
		}
		return deviceRssiLqiMap;
	}
	
	private static byte[] eneRssiInfos(Map<String, Object> value) {
		return null;
	}
	
	private static Map<String, Object> anaPinIoInfos(byte[] data) {
		int interval = 8;
		byte[] pint = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		byte[] dirt = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		byte[] elet = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		int len = (data.length - 1) / 3;
		byte fag = data[0];
		if (0 < fag) {
			int j = data.length - len;
			for (int i = 0; i < interval; i++) {
				if (0x01 == (0x01 & fag >> i)) {
					pint[i] = data[j];
					dirt[i] = data[j - len];
					elet[i] = data[j - len - len];
					j++;
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < 64; i++) {
			if (0x01 == (0x01 & pint[i / 8])) {
				map.put(Integer.toString(i), (0x01 & dirt[i / 8]) + " " + (0x01 & elet[i / 8]));
			}
			pint[i / 8] >>= 1;
			dirt[i / 8] >>= 1;
			elet[i / 8] >>= 1;
		}
		return map;
	}
	
	private static byte[] enePinIoInfos(Map<String, Object> value) {
		byte flag = 0x00;
		for (Iterator<Map.Entry<String, Object>> ite = value.entrySet().iterator(); ite.hasNext();) {
			Map.Entry<String, Object> entry = (Entry<String, Object>) ite.next();
			int pin = Public.objToInt(entry.getKey());
			if (0 > pin || 63 < pin)
				continue;
			flag |= (1 << (pin / 8));
		}
		int n = 0;
		for (int i = 0; i < 8; i++) {
			if (1 == (flag >> i & 1)) {
				n++;
			}
		}
		int length = 1 + 3 * n;
		ByteBuffer src = ByteBuffer.allocate(length);
		src.put(flag);
		ByteBuffer pins = ByteBuffer.allocate(n);
		ByteBuffer dirs = ByteBuffer.allocate(n);
		ByteBuffer eles = ByteBuffer.allocate(n);
		boolean hasactionpin = false;
		for (Iterator<Map.Entry<String, Object>> ite = value.entrySet().iterator(); ite.hasNext();) {
			Map.Entry<String, Object> entry = (Entry<String, Object>) ite.next();
			int pin = Public.objToInt(entry.getKey());
			if (0 > pin || 63 < pin)
				continue;
			String[] vs = entry.getValue().toString().split(" ");
			int dir = Public.objToInt(vs[0]);
			int val = Public.objToInt(vs[1]);
			
			int j = -1;
			for (int ii = 0; ii < (pin / 8 + 1); ii++) {
				if (1 == (flag >> ii & 1)) {
					j++;
				}
			}
			if (-1 < j) {
				hasactionpin = true;
				// 电平位
				if (0 < val)
					eles.put(j, (byte) (eles.get(j) | (1 << (pin - (pin / 8 * 8)))));
				else
					eles.put(j, (byte) (eles.get(j) & ~(1 << (pin - (pin / 8 * 8)))));
				// 方向位
				if (0 < dir)
					dirs.put(j, (byte) (dirs.get(j) | (1 << (pin - (pin / 8 * 8)))));
				else
					dirs.put(j, (byte) (dirs.get(j) & ~(1 << (pin - (pin / 8 * 8)))));
				// 引脚位
				pins.put(j, (byte) (pins.get(j) | (1 << (pin - (pin / 8 * 8)))));
			}
		}
		if (hasactionpin) {
			src.put(eles);
			src.put(dirs);
			src.put(pins);
			return src.array();
		}
		return null;
	}
	
	private static Map<String, Object> anaPinADInfos(byte[] data) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < data.length; i += 5) {
			double v = 15.119 * Public.bytes2Int(new byte[] { data[i + 1], data[i + 2], data[i + 3], data[i + 4] }) + 93.558;
			BigDecimal b = new BigDecimal(v);
			v = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			map.put(Integer.toString(Public.bytes2Int(new byte[] { data[i] })), v);
		}
		return map;
	}
	
	private static byte[] enePinADInfos(Map<String, Object> value) {
		ByteBuf src = Unpooled.buffer(28);
		for (Iterator<Map.Entry<String, Object>> ite = value.entrySet().iterator(); ite.hasNext();) {
			Map.Entry<String, Object> entry = (Entry<String, Object>) ite.next();
			int pn = Public.objToInt(entry.getKey());
			int dt = (int) ((Public.objToDouble(entry.getValue()) - 93.558) / 15.119);
			src.writeByte(pn);
			src.writeBytes(Public.int2Bytes(dt, 4));
		}
		byte[] data = new byte[src.readableBytes()];
		src.readBytes(data);
		return data;
	}
	
	private static String bytes2UnicodeStr(byte[] data) {
		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		int i = 0;
		for (i = 0; i < data.length; i++) {
			if (i + 1 < data.length) {
				buffer.put(data[i + 1]);
				buffer.put(data[i]);
				i++;
			} else {
				buffer.put(data[i]);
			}
		}
		try {
			return new String(buffer.array(), "UNICODE");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static byte[] unicodeStr2Bytes(String value) {
		byte[] data = new byte[] { (byte) 0x00 };
		try {
			data = value.getBytes("UNICODE");
			for (int i = 0; i < data.length; i++) {
				if (i + 1 < data.length) {
					byte t = data[i];
					data[i] = data[i + 1];
					data[i + 1] = t;
					i++;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	private static String bytes2TimeStr(byte[] data) {
		long intime = (Public.bytes2Int(data) - 8 * 60 * 60) * 1000L;
		Date date = new Date(intime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
	
	private static byte[] timeStr2Bytes(String value) {
		Date date = new Date();
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = format.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int dt = (int) (date.getTime() / 1000L + 8 * 60 * 60);
		return Public.int2Bytes(dt, 4);
	}
	
	public static Object convertByteBuf2TypeValue(String type, ByteBuf value) {
		byte data[] = new byte[value.readableBytes()];
		value.readBytes(data);
		switch (convertTypeStr2TypeId(type)) {
		case DATATYPE_BYTE:
			return Public.byte2hex(Public.bytesOverturn(data)).replace(" ", "");
		case DATATYPE_INT:
			return Integer.toString(Public.bytes2Int(data));
		case DATATYPE_FLOAT:
			return bytes2FloatStr(data);
		case DATATYPE_CHAR:
			return bytes2String(data);
		case DATATYPE_IP:
			return bytes2IpStr(data);
		case DATATYPE_NEGATIVE_INT:
			return Integer.toString(0 - Public.bytes2Int(data));
		case DATATYPE_RSDATA:
			return anaRssiInfos(data);
		case DATATYPE_PIODATA:
			return anaPinIoInfos(data);
		case DATATYPE_PADDATA:
			return anaPinADInfos(data);
		case DATATYPE_UNICODE:
			return bytes2UnicodeStr(data);
		case DATATYPE_INTIME:
			return bytes2TimeStr(data);
		default:
			return Public.byte2hex(data);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Byte> convertTypeValue2ByteBuf(String type, Object value) {
		List<Byte> xlt = new ArrayList<Byte>();
		byte[] data = null;
		switch (convertTypeStr2TypeId(type)) {
		case DATATYPE_BYTE:
		    data = Public.bytesOverturn(Public.hexString2bytes(value.toString()));
		    for (int i = 0; i < data.length; i++) {
		        xlt.add(data[i]);
		    }
			return xlt;
		case DATATYPE_INT:
		    xlt.add((byte) Public.objToInt(value));
		    return xlt;
		case DATATYPE_FLOAT:
            data = floatStr2Bytes(value.toString());
            for (int i = 0; i < data.length; i++) {
                xlt.add(data[i]);
            }
            return xlt;
		case DATATYPE_CHAR:
            data = string2Bytes(value.toString());
            for (int i = 0; i < data.length; i++) {
                xlt.add(data[i]);
            }
            return xlt;
		case DATATYPE_IP:
            data = ipStr2Bytes(value.toString());
            for (int i = 0; i < data.length; i++) {
                xlt.add(data[i]);
            }
            return xlt;
		case DATATYPE_NEGATIVE_INT:
            xlt.add((byte) (0 - Public.objToInt(value)));
            return xlt;
		case DATATYPE_RSDATA:
            data = eneRssiInfos((Map<String, Object>) value);
            for (int i = 0; i < data.length; i++) {
                xlt.add(data[i]);
            }
            return xlt;
		case DATATYPE_PIODATA:
            data = enePinIoInfos((Map<String, Object>) value);
            for (int i = 0; i < data.length; i++) {
                xlt.add(data[i]);
            }
            return xlt;
		case DATATYPE_PADDATA:
            data = enePinADInfos((Map<String, Object>) value);
            for (int i = 0; i < data.length; i++) {
                xlt.add(data[i]);
            }
            return xlt;
		case DATATYPE_UNICODE:
            data = unicodeStr2Bytes(value.toString());
            for (int i = 0; i < data.length; i++) {
                xlt.add(data[i]);
            }
            return xlt;
		case DATATYPE_INTIME:
            data = timeStr2Bytes(value.toString());
            for (int i = 0; i < data.length; i++) {
                xlt.add(data[i]);
            }
            return xlt;
		default:
            data = Public.serializer(value);
            for (int i = 0; i < data.length; i++) {
                xlt.add(data[i]);
            }
            return xlt;
		}
	}
	
}
