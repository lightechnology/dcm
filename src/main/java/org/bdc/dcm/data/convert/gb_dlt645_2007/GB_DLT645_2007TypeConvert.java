package org.bdc.dcm.data.convert.gb_dlt645_2007;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.data.coder.intf.TypeConvert;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class GB_DLT645_2007TypeConvert implements TypeConvert {

	public static final List<Field> fields = Arrays.asList(new Field[]{
			
			new Field("01040000",8,Arrays.asList(new Attr("组合无功 2 总最大需量","kvar","n","##.####"),new Attr("发送时间","yy年MM月dd日HH时mm分","t","yyMMddHHmm"))),
			
			new Field("02010100",2,Arrays.asList(new Attr("A 相电压","V","n","###.#"))),
			new Field("02010200",2,Arrays.asList(new Attr("B 相电压","V","n","###.#"))),
			new Field("02010300",2,Arrays.asList(new Attr("C 相电压","V","n","###.#"))),
			new Field("0201FF00",2,Arrays.asList(new Attr("电压数据块","V","n","###.#"))),
			
			new Field("02020100",3,Arrays.asList(new Attr("A 相电流","A","n","###.###"))),
			new Field("02020200",3,Arrays.asList(new Attr("B 相电流","A","n","###.###"))),
			new Field("02020300",3,Arrays.asList(new Attr("C 相电流","A","n","###.###"))),
			new Field("0202FF00",3,Arrays.asList(new Attr("电流数据块","A","n","###.###"))),
			
			new Field("02020000",3,Arrays.asList(new Attr("瞬时总有功功率","KW","n","##.####"))),
			new Field("02020100",3,Arrays.asList(new Attr("瞬时 A 相有功功率","KW","n","##.####"))),
			new Field("02020200",3,Arrays.asList(new Attr("瞬时 B 相有功功率","KW","n","##.####"))),
			new Field("02020300",3,Arrays.asList(new Attr("瞬时 C 相有功功率","KW","n","##.####"))),
			new Field("0202FF00",3,Arrays.asList(new Attr("瞬时有功功率数据块","KW","n","##.####")))
			
			
	});
	private static Map<String,Integer> map = new HashMap<>();
	
	static{
		//系统支持字段 映射为转码器字段
		map.put("a", 0);
	}
	public static String addPoint(int num,int index){
		String str = num+"";
		String result = "";
		for(int i=0;i<str.length();i++){
			if(i==index)
				result+=".";
			result+=str.charAt(i);
				
		}
		return result;
	}
	@Override
	public Object decode(String type, ByteBuf in) {
		StringBuffer result = new StringBuffer();
		try {
			Field field = fields.get(map.get(type));
			List<Attr> attrs = field.attrs;
			byte[] tmp = new byte[in.readableBytes()];
			in.getBytes(0, tmp);
			System.out.println(Public.byte2hex(tmp));
			for(int i=0;i<attrs.size();i++){
				Attr attr = attrs.get(i);
				byte[] data = new byte[attr.len];
				
				in.readBytes(data);
				if(attr.type.equals(AttrType.t)){
					String dateStr = "";
					for(int j=0;j<data.length;j++){
						byte b = data[j];
						if( b < 10)
							dateStr+=("0"+data[j]);
						else
							dateStr+=(data[j]&0xff);
					}
					SimpleDateFormat sdf= ((SimpleDateFormat)attr.format);
					Date date = sdf.parse(dateStr);
					result.append(new SimpleDateFormat(attr.unit).format(date));
				}else{
					if(attr.pointIndex == -1){
						result.append(Public.bytes2Int(data)+"");
					}else{
						String num = addPoint(Public.bytes2Int(data), attr.pointIndex);
						System.err.println(num);
						result.append(((DecimalFormat)attr.format).parse(num));
					}
				}
				if(i < attrs.size() - 1)
					result.append(",");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result.toString();
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
	
	public String hexStrIndentity;
	
	public int len;
	
	public List<Attr> attrs = new ArrayList<>();
	
	public Field(String hexStrIndentity, int len,List<Attr> attrs) {
		super();
		this.hexStrIndentity =hexStrIndentity;
		this.attrs = attrs;
		this.len = len;
		System.err.println(this);
	}

	@Override
	public String toString() {
		return "Field [hexStrIndentity=" + hexStrIndentity + ", len=" + len + ", attrs=" + attrs + "]";
	}
	
	
}
