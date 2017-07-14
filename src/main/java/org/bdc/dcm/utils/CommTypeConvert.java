package org.bdc.dcm.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.bdc.dcm.vo.DataTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;

/**
 * <table>
 * 	<tr>
 *		<td>calc</td>
 *		<td>英文</td>
 *		<td>缩写</td>
 * 	</tr>
 *	<tr>
 *		<td>加</td>
 *		<td>add</td>
 *		<td>a</td>
 * 	</tr><tr>
 *		<td>减</td>
 *		<td>minus</td>
 *		<td>m</td>
 * 	</tr><tr>
 *		<td>乘</td>
 *		<td>time</td>
 *		<td>t</td>
 * 	</tr>
 * 	<tr>
 *		<td>除</td>
 *		<td>divide</td>
 *		<td>d</td>
 * 	</tr>
 * </table>
 * @author Administrator
 *
 */
public abstract class CommTypeConvert {

	private  Logger logger = LoggerFactory.getLogger(CommTypeConvert.class); 
	
	/**
	 * 执行方法后的后续处理
	 */
	protected  Map<Integer,Function<Number, ?>> typeToBackConvert= new HashMap<>();
	
	/**
	 * 用于类型找到对应的bytebuf执行的方法 肯定返回数字类型 Number
	 */
	protected static Map<String,Method> typeToMethod = new HashMap<>();
	
	protected  Function<Number, Double> d10 = null;
	protected  Function<Number, Double> d100 = null;
	protected  Function<Number, Double> d1000 = null;
	protected  Function<Number, Double> d3200 = null;
	protected  Function<Number, Boolean> numberToBoolean = null;
	
	protected CommTypeConvert(){
		try {
			initFun();
			initTypeToMethod();
			initTypeTokey();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private  void initTypeToMethod() throws Exception{
		
		Class<AbstractByteBuf> clazz = AbstractByteBuf.class;
		typeToMethod.put("boolean", clazz.getDeclaredMethod("readBoolean"));
		typeToMethod.put("byte", clazz.getDeclaredMethod("readByte"));
		typeToMethod.put("uByte", clazz.getDeclaredMethod("readUnsignedByte"));
		typeToMethod.put("short", clazz.getDeclaredMethod("readShort"));
		typeToMethod.put("uShort", clazz.getDeclaredMethod("readUnsignedShort"));
		typeToMethod.put("medium", clazz.getDeclaredMethod("readMedium"));
		typeToMethod.put("uMedium", clazz.getDeclaredMethod("readUnsignedMedium"));
		typeToMethod.put("int", clazz.getDeclaredMethod("readInt"));
		typeToMethod.put("uInt", clazz.getDeclaredMethod("readUnsignedInt"));
		typeToMethod.put("long", clazz.getDeclaredMethod("readLong"));
		typeToMethod.put("char", clazz.getDeclaredMethod("readChar"));
		typeToMethod.put("float", clazz.getDeclaredMethod("readFloat"));
		typeToMethod.put("double", clazz.getDeclaredMethod("readDouble"));
	}
	/**
	 * 初始化功能代码
	 */
	private void initFun(){
		d10 = (obj)->{
			return Double.valueOf(obj.doubleValue()/10);
		};
		d100 = (obj)->{
			return Double.valueOf(obj.doubleValue()/100);
		};
		d1000 = (obj)->{
			return Double.valueOf(obj.doubleValue()/1000);
		};
		d3200 = (obj)->{
			return Double.valueOf(obj.doubleValue()/3200);
		};
		numberToBoolean = (obj)->{
			return obj.doubleValue()>0;
		};
	}
	public Object convertByteBuf2TypeValue(DataTab tab, ByteBuf in) {

		in.markReaderIndex();
		
		try {
			Method m = typeToMethod.get(tab.getForm());
			if(m != null){
				Number obj = (Number) m.invoke(in, null);
				Function<Number, ?> fun = typeToBackConvert.get(tab.getName());
				if(fun != null){
					return fun.apply(obj);
				}
				return obj.doubleValue();
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected  void reverse(byte[] bs){
		byte tmp ;
		for(int i=0;i<bs.length/2;i++){
			tmp = bs[i];
			bs[i] = bs[bs.length - 1 -i];
			bs[bs.length - 1 - i] = tmp;
		}
	}
	protected abstract void initTypeTokey() throws Exception;

}
