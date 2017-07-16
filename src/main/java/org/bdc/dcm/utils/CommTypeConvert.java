package org.bdc.dcm.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.bdc.dcm.vo.DataModel;
import org.bdc.dcm.vo.DataTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;

/**
 * <table>
 * <tr>
 * <td>calc</td>
 * <td>英文</td>
 * <td>缩写</td>
 * </tr>
 * <tr>
 * <td>加</td>
 * <td>add</td>
 * <td>a</td>
 * </tr>
 * <tr>
 * <td>减</td>
 * <td>minus</td>
 * <td>m</td>
 * </tr>
 * <tr>
 * <td>乘</td>
 * <td>time</td>
 * <td>t</td>
 * </tr>
 * <tr>
 * <td>除</td>
 * <td>divide</td>
 * <td>d</td>
 * </tr>
 * </table>
 * 
 * @author Administrator
 *
 */
public abstract class CommTypeConvert {

	private Logger logger = LoggerFactory.getLogger(CommTypeConvert.class);

	/**
	 * 执行方法后的后续处理
	 */
	protected Map<Integer, Function<Number, ?>> typeToBackConvert = new HashMap<>();

	/**
	 * 用于类型找到对应的bytebuf执行的方法 肯定返回数字类型 Number
	 */
	protected static Map<String, Method[]> typeToMethodInRW = new HashMap<>();

	protected Function<Number, Double> d10 = null;
	protected Function<Number, Double> d100 = null;
	protected Function<Number, Double> d1000 = null;
	protected Function<Number, Double> d3200 = null;
	protected Function<Number, Boolean> numberToBoolean = null;

	protected Function<Number, Double> t10 = null;
	protected Function<Number, Double> t100 = null;
	protected Function<Number, Double> t1000 = null;
	protected Function<Number, Double> t3200 = null;

	protected CommTypeConvert() {
		try {
			initFun();
			initTypeToMethod();
			initRegTokey();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initTypeToMethod() throws Exception {

		Class<AbstractByteBuf> clazz = AbstractByteBuf.class;
		
		typeToMethodInRW.put("boolean", new Method[] { 
					clazz.getDeclaredMethod("readBoolean"),
					clazz.getDeclaredMethod("writeBoolean", boolean.class) 
				});
		
		typeToMethodInRW.put("byte", new Method[] { 
					clazz.getDeclaredMethod("readByte"),
					clazz.getDeclaredMethod("writeByte",int.class) 
				});
		
		typeToMethodInRW.put("uByte", new Method[] { 
					clazz.getDeclaredMethod("readUnsignedByte"),
					clazz.getDeclaredMethod("writeByte",int.class)  
				});
		
		typeToMethodInRW.put("short", new Method[] { 
					clazz.getDeclaredMethod("readShort"),
					clazz.getDeclaredMethod("writeshort",int.class)  
				});
		
		typeToMethodInRW.put("uShort", new Method[] { 
					clazz.getDeclaredMethod("readUnsignedShort"),
					clazz.getDeclaredMethod("writeshort",int.class)  
				});
		
		typeToMethodInRW.put("medium", new Method[] { 
					clazz.getDeclaredMethod("readMedium"),
					clazz.getDeclaredMethod("writeMedium",int.class)  
				});
		
		typeToMethodInRW.put("uMedium", new Method[] { 
					clazz.getDeclaredMethod("readUnsignedMedium"),
					clazz.getDeclaredMethod("writeMedium",int.class)  
				});
		
		typeToMethodInRW.put("int", new Method[] { 
					clazz.getDeclaredMethod("readInt"),
					clazz.getDeclaredMethod("writeInt",int.class)  
				});
		
		typeToMethodInRW.put("uInt", new Method[] { 
					clazz.getDeclaredMethod("readUnsignedInt"),
					clazz.getDeclaredMethod("writeInt",int.class)  
				});
		
		typeToMethodInRW.put("long", new Method[] { 
					clazz.getDeclaredMethod("readLong"),
					clazz.getDeclaredMethod("writeLong",long.class)  
				});
		
		typeToMethodInRW.put("char", new Method[] { 
					clazz.getDeclaredMethod("readChar"),
					clazz.getDeclaredMethod("writeChar",int.class)  
				});
		
		typeToMethodInRW.put("float", new Method[] { 
					clazz.getDeclaredMethod("readFloat"),
					clazz.getDeclaredMethod("writeFloat",float.class)  
				});
		
		typeToMethodInRW.put("double", new Method[] { 
					clazz.getDeclaredMethod("readDouble"),
					clazz.getDeclaredMethod("writeDouble",double.class)  
				});
	}

	/**
	 * 初始化功能代码
	 */
	private void initFun() {
		d10 = (obj) -> {
			return Double.valueOf(obj.doubleValue() / 10);
		};
		d100 = (obj) -> {
			return Double.valueOf(obj.doubleValue() / 100);
		};
		d1000 = (obj) -> {
			return Double.valueOf(obj.doubleValue() / 1000);
		};
		d3200 = (obj) -> {
			return Double.valueOf(obj.doubleValue() / 3200);
		};
		numberToBoolean = (obj) -> {
			return obj.doubleValue() > 0;
		};
		t10 = (obj) -> {
			return Double.valueOf(obj.doubleValue() * 10);
		};
		t100 = (obj) -> {
			return Double.valueOf(obj.doubleValue() * 100);
		};
		t1000 = (obj) -> {
			return Double.valueOf(obj.doubleValue() * 1000);
		};
		t3200 = (obj) -> {
			return Double.valueOf(obj.doubleValue() * 3200);
		};
	}
	/**
	 * 通用 包装类 得到 基本类型simpleName
	 * @param o
	 * @return
	 */
	public String paramMapToBaseTypeSimpleName(Object o){
		switch(o.getClass().getSimpleName()){
			case "Integer":return "int";
			case "Character": return "char";
			case "Byte":return "byte";
			case "Short":return "short";
			case "Boolean":return "boolean";
			case "Long":return "long";
			case "Float":return "float";
			case "Double":return "double";
		default :return null;
	}
	}
	/**
	 * 获取包装类型的基本类型simpleName
	 * 如果需要 可以复写
	 * @param o
	 * @return
	 */
	public Class<?> paramMapToBaseClass(Object o){
		switch(o.getClass().getSimpleName()){
			case "Integer":return int.class;
			case "Character":return char.class;
			case "Byte":return byte.class;
			case "Short":return short.class;
			case "Boolean":return boolean.class;
			case "Long":return long.class;
			case "Float":return float.class;
			case "Double":return double.class;
			default :return null;
		}
	};
	/**
	 * 获取包装类型的基本类型 netty 转解
	 * 如果需要 可以复写
	 * @param o
	 * @return
	 */
	Function<Object,Class<?>> writeParmMapToBaseType = (o)->{
		switch(o.getClass().getSimpleName()){
			case "Integer":
			case "Character":
			case "Byte":
			case "Short":return int.class;
			case "Boolean":return boolean.class;
			case "Long":return long.class;
			case "Float":return float.class;
			case "Double":return double.class;
			default :return null;
		}
	};
	
	/**
	 * 通用连续地址内容写
	 * @param dataModelList 这里请填入一组功能下连续地址的datatab
	 * @param out
	 * @param addr
	 * @param func
	 * @param val
	 * @return
	 */
	public ByteBuf write(List<DataModel> dataModelList,ByteBuf out){
		ByteBuf buf = null;
		for(DataModel tab:dataModelList){
			ByteBuf tmp = write(tab, out);
			if(tmp != null)
				buf = tmp;
		}
		return buf;
	} 
	
	private ByteBuf wirte(DataModel dataModel,ByteBuf out,Function<Object,Class<?>> fun){
		try {
			final int writeMethodIndex = 1;
			Method[] m = typeToMethodInRW.get(dataModel.getForm());
			if(m != null && m[writeMethodIndex] != null){
				Object val = dataModel.getVal();
				int count = m[writeMethodIndex].getParameterCount();
				if(count == 1){
					Class<?> clazz = m[writeMethodIndex].getParameterTypes()[0];
					//用户传入参数的类型  与 用户定义的方法数据类型
					if(fun.apply(val).equals(clazz) ){
						return (ByteBuf)m[writeMethodIndex].invoke(out, val);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	/**
	 * 通用单地址内容写
	 * @param dataModel 这里请填入一组功能下连续地址的datatab
	 * @param out
	 * @param val
	 * @return
	 */
	public ByteBuf write(DataModel dataModel,ByteBuf out){
		return wirte(dataModel,out,writeParmMapToBaseType);
	}
	/**
	 * 依据配置读内容
	 * @param tab
	 * @param in
	 * @return
	 */
	public Object read(DataTab tab, ByteBuf in) {

		in.markReaderIndex();

		try {
			Method[] m = typeToMethodInRW.get(tab.getForm());
			if (m != null && m[0] != null) {
				Number obj = (Number) m[0].invoke(in, null);
				Function<Number, ?> fun = typeToBackConvert.get(tab.getName());
				if (fun != null) {
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

	protected void reverse(byte[] bs) {
		byte tmp;
		for (int i = 0; i < bs.length / 2; i++) {
			tmp = bs[i];
			bs[i] = bs[bs.length - 1 - i];
			bs[bs.length - 1 - i] = tmp;
		}
	}
	/**
	 * 寄存器转换后算法
	 * @throws Exception
	 */
	protected abstract void initRegTokey() throws Exception;
	
}
