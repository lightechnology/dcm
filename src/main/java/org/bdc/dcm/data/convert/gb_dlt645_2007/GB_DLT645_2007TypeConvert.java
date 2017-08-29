package org.bdc.dcm.data.convert.gb_dlt645_2007;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bdc.dcm.data.coder.utils.CommUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

import static org.bdc.dcm.netty.framer.Gb_dlt645_2007FrameDecoder.ZJZD_MAC_LEN;;
public class GB_DLT645_2007TypeConvert {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public static List<Field> fields = new ArrayList<>();
	public static Map<String,Integer> sysFieldMapToConvertFieldIndex = new HashMap<>();
	private final static GB_DLT645_2007TypeConvert convert = new GB_DLT645_2007TypeConvert();
	static{
		String basePath = "org/bdc/dcm/data/convert/gb_dlt645_2007/";
		//系统支持字段 映射为转码器字段
		fields.addAll(fieldDecoderByJsonFile(basePath+"fieldConfig.json"));
		sysFieldMapToConvertFieldIndex.putAll(sysFieldToMapConvertField(fields,basePath+"sysFileldToMap.json"));
	}
	public static GB_DLT645_2007TypeConvert getConvert() {
		return convert;
	}
	private GB_DLT645_2007TypeConvert(){}
	/**
	 * <h1>系统字段映射为转换器字段</h1>
	 * key 系统字段</br>
	 * val 转换器字段下标
	 * @param fields 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String,Integer> sysFieldToMapConvertField(List<Field> fields, String jsonPath){
		//配置文件json信息
		JSONObject json = Public.str2Json(CommUtils.getJsonStr(GB_DLT645_2007TypeConvert.class.getClassLoader().getResourceAsStream(jsonPath)));
		Map<String,Integer> map = new HashMap<>();
		//配置文件所有第一级key
		Set<String> keys = json.keySet();
		Iterator<String> iterator = keys.iterator();
		StringBuffer sb = new StringBuffer();
		while(iterator.hasNext()){
			String key = iterator.next();
			String val = CommUtils.GB_2007DataTrans((String) json.get(key),0);
			//减少内存
			sb.delete(0,sb.length());   
			int index = -1;
			for(int i=0;i<fields.size();i++){
				Field f = fields.get(i);
				if(f !=null & f.getHexStrIndentity().equals(val)){
					index = i;
					f.sysFieldType = key;
				}
			}
			if(index > -1)
				map.put(key, index);
		}
		return map;
		
	}
	/**
	 * 从json文件解码出配置
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<Field> fieldDecoderByJsonFile(String jsonPath){
		JSONObject json = Public.str2Json(CommUtils.getJsonStr(GB_DLT645_2007TypeConvert.class.getClassLoader().getResourceAsStream(jsonPath)));
		Iterator<String> iterator = json.keySet().iterator();
		List<Field> fields = new ArrayList<>();
		while(iterator.hasNext()){
			String key = iterator.next();
			Map<String,Object> map =  (Map<String, Object>) json.get(key);
			long len = (long) map.get("len");
			List<String> attrStrArr = (List<String>) map.get("attrs");
			List<Attr> attrs = attrStrArr.stream().map(new Function<String, Attr>() {

				@Override
				public Attr apply(String t) {
					String[] strArr = t.split(",");
					if(strArr.length == 4)
						return new Attr(strArr[0], strArr[1], strArr[2], strArr[3]);
					else
						return null;
				}
			}).collect(Collectors.toList());
			fields.add(new Field(key, (int)len, attrs));
		}
		return fields;
	}
	
	/**
	 * 统一编码入口
	 * @param type
	 * @param val
	 * @param gbAddr
	 * @return
	 * @throws IllegalAccessException 
	 */
	public byte[] encoder(String type, Object val,byte[] gbAddr) throws IllegalAccessException{
		return applyData(type, val, gbAddr);
	}
	
	/**
	 * 统一解码入口
	 * @param type 统一系统字段类型
	 */
	public Map<String,Object> decode(String type, ByteBuf in) {
		Map<String,Object> outMap = new HashMap<>();
		StringBuffer result = new StringBuffer();
		Field field = null;
		try {
			field = fields.get(sysFieldMapToConvertFieldIndex.get(type));
			decodeByField(in, result, field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		outMap.put(type,CommUtils.makeMapValue(field != null ? field.name:"", result.toString()));
		return outMap;
	}
	
	/**
	 * 通过解析数据流中的地址解码
	 * @param reverseFieldAddr
	 * @param in
	 * @return
	 */
	public Map<String,Object> decodeByDataStreamAddr(final String reverseFieldAddr,ByteBuf in){
		Map<String,Object> outMap = new HashMap<>();
		StringBuffer result = new StringBuffer();
		Field field = null;
		try {
			//找到字段
			Optional<Field> optional = fields.stream().filter(item->item.getHexStrIndentity().equals(reverseFieldAddr)).findFirst();
			if(optional.isPresent()){
				field = optional.get();
				decodeByField(in, result, field );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(field != null){
			outMap.put(field.sysFieldType, result.toString());
		}
		return outMap;
	}
	/**
	 * 请求读电能表数据,只做读命令解析
	 * @param type
	 * @param val
	 * @param addr 4字节数据标识
	 * @return
	 * @throws IllegalAccessException
	 */
	private byte[] applyData(String type, Object val,byte[] addr) throws IllegalAccessException{
		
		if(val != null){
			logger.error("输入参数 【2】 不进行处理,值：",val);
		}
		if(addr.length != ZJZD_MAC_LEN)
			throw new IllegalAccessException("请输入【"+ZJZD_MAC_LEN+"】个字节地址");
		Integer filedOrder = sysFieldMapToConvertFieldIndex.get(type);
		if(filedOrder == null)
			return new byte[0];
		Field field = fields.get(filedOrder);
		ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.buffer();
		buf.writeByte((byte)0xfe);
		buf.writeByte((byte)0xfe);
		buf.writeByte((byte)0xfe);
		buf.writeByte((byte)0xfe);
		buf.writeByte((byte)0x68);
		buf.writeBytes(addr);
		buf.writeByte((byte)0x68);
		buf.writeByte((byte)0x11);
		buf.writeByte((byte)0x04);
		byte[] src = Public.hexString2bytes(field.getHexStrIndentity());
		buf.writeBytes(src);
		
		byte[] out = new byte[buf.readableBytes()];
		buf.getBytes(4, out);
		buf.writeByte(Public.int2Bytes(CommUtils.checkSum(out, 0, 14), 1)[0]);
		
		buf.writeByte((byte)0x16);
		out = new byte[buf.readableBytes()];
		buf.readBytes(out);
		return out;
	}
	private void decodeByField(ByteBuf in, StringBuffer result, Field field) throws ParseException {
		List<Attr> attrs = field.attrs;
		int max = attrs.size();
		for(int i=0;i<max;i++){
			Attr attr = attrs.get(i);
			byte[] data = new byte[attr.len];
			in.readBytes(data);
			
			CommUtils.GB_2007DataTrans(data, 1);
			
			//时间格式化
			if(attr.type.equals(AttrType.t)){
				String dateStr = "";
				for(int j=0;j<data.length;j++){
					byte b = data[j];
					if( b < 10)
						dateStr+=("0"+b);
					else
						dateStr+=(b&0xff);
				}
				SimpleDateFormat sdf= ((SimpleDateFormat)attr.format);
				Date date = sdf.parse(dateStr);
				result.append(new SimpleDateFormat(attr.unit).format(date));
			}else{//数字格式化
				StringBuffer sb = new StringBuffer();
				for(byte b:data){
					sb.append(Public.byte2hex_ex(b));
				}
				if(attr.pointIndex == -1){
					result.append(sb);
				}else{
					String numStr = sb.toString();
					String num = CommUtils.addPoint(numStr, attr.pointIndex);
					String numberStr = "";
					try{//可能返回的不是数字 是abcdef 其实就是可能存在 FF FF 说明表不支持
						DecimalFormat format = ((DecimalFormat)attr.format);
						numberStr = format.format(format.parse(num));
					}catch(Exception e){
						
					}
					result.append(numberStr);
				}
				result.append(" ").append(attr.unit);
			}
			if(i < attrs.size() - 1)
				result.append(",");
		}
	}
}
enum AttrType{
	/**
	 * time
	 */
	t,
	/**
	 * number
	 */
	n
}
class Attr{
	private static final List<Character> dateFormatKeyWords =  Arrays.asList(new Character[]{
			'y','M','d','h','h','H','M','m','s','S','E','D','F','w','W','a','K','k'
	});
	private static final List<Character> numberFormatKeyWords =  Arrays.asList(new Character[]{
			'#','0'
	});
	public AttrType type;
	public Format format;
	public String name;
	public Object val;
	public String unit;
	public int len;
	/**
	 * 小数点位置
	 */
	public int pointIndex = -1;
	public Attr(String name, String unit,String type, String format) {
		super();
		this.type = AttrType.valueOf(type);
		this.format = this.type.equals(AttrType.t)?new SimpleDateFormat(format):new DecimalFormat(format);
		this.len = calcByteLen(format);
		this.name = name;
		this.unit = unit;
	}
	
	@Override
	public String toString() {
		return "Attr [type=" + type + ", format=" + format + ", name=" + name + ", val=" + val + ", unit=" + unit
				+ ", len=" + len + ", pointIndex=" + pointIndex + "]";
	}

	public int calcByteLen(String format){
		int len = 0;
		if(type.equals(AttrType.n)){
			for(int i=0;i<format.length();i++){
				Character c = format.charAt(i);
				if( numberFormatKeyWords.contains(c) )
					len++;	
				else if(format.charAt(i) == '.')
					pointIndex = i;
			}
		}else{
			for(int i=0;i<format.length();i++)
				if(dateFormatKeyWords.contains(format.charAt(i)))
					len++;	
		}
		return len/2;
	}
}
class Field{
	/**
	 * 数据项名称
	 */
	public String name;

	/**
	 * 系统字段类型
	 */
	public String sysFieldType;
	
	/**
	 * 已经被反序 而且加 33H
	 */
	private String hexStrIndentity;
	
	/**
	 * 字段总长度
	 */
	public int len;
	
	public List<Attr> attrs = new ArrayList<>();
	/**
	 * 
	 * @param hexStrIndentity 正序进 反序出+33H
	 * @param len
	 * @param attrs
	 */
	public Field(String hexStrIndentity, int len,List<Attr> attrs) {
		super();
		this.hexStrIndentity = CommUtils.GB_2007DataTrans(hexStrIndentity,0);
		this.attrs = attrs;
		this.len = len;
		
		//通过属性名字构造字段名字
		int max = attrs.size();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<max;i++){
			sb.append(attrs.get(i).name);
			if(i < max - 1){
				sb.append("及");
			}
		}
		this.name = sb.toString();
		System.err.println(this);
	}
	/**
	 * 已经被反序 而且加 33H
	 * @return
	 */
	public String getHexStrIndentity() {
		return hexStrIndentity;
	}

	@Override
	public String toString() {
		return "Field [name=" + name + ", hexStrIndentity=" + hexStrIndentity + ", len=" + len + ", attrs=" + attrs
				+ "]";
	}
		
}
