package org.bdc.dcm.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.bdc.dcm.vo.DataModel;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.FunIndentity;
import org.bdc.dcm.vo.e.DataType;
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
	 * 
	 * key 寄存器地址
	 * val 内部进行需要的计算
	 * 用于类型找到对应的bytebuf执行的方法 肯定返回数字类型 Number
	 * 运行时调用
	*/
	protected Map<FunIndentity, Function<byte[], byte[]>> typeToBackConvert = new HashMap<>();
	private static Map<String, Method[]> typeToMethodInRW = new HashMap<>();

	protected CommTypeConvert() {
		try {
			initTypeToMethod();
			this.typeToBackConvert.putAll(getTypeToBackConvert());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 类初始化调用 在读后进行数据转换的接接口
	 * @return
	 */
	public abstract Map<FunIndentity, Function<byte[], byte[]>> getTypeToBackConvert();
	
	private void initTypeToMethod() throws Exception {

		Class<AbstractByteBuf> clazz = AbstractByteBuf.class;
		typeToMethodInRW.put("boolean", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeBoolean", boolean.class) 
		});
		
		typeToMethodInRW.put("byte", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeByte",int.class) 
		});
		
		typeToMethodInRW.put("uByte", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeByte",int.class)  
		});
		
		typeToMethodInRW.put("short", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeShort",int.class)  
		});
		
		typeToMethodInRW.put("uShort", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeShort",int.class)  
		});
		
		typeToMethodInRW.put("medium", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeMedium",int.class)  
		});
		
		typeToMethodInRW.put("uMedium", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeMedium",int.class)  
		});
		
		typeToMethodInRW.put("int", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeInt",int.class)  
		});
		
		typeToMethodInRW.put("uInt", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeInt",int.class)  
		});
		
		typeToMethodInRW.put("long", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeLong",long.class)  
		});
		
		typeToMethodInRW.put("char", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeChar",int.class)  
		});
		
		typeToMethodInRW.put("float", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeFloat",float.class)  
		});
		
		typeToMethodInRW.put("double", new Method[] { 
			clazz.getDeclaredMethod("readBytes",int.class),
			clazz.getDeclaredMethod("writeDouble",double.class)  
		});
		//继承
		typeToMethodInRW.put("crcSum16",typeToMethodInRW.get("byte"));
		
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
	public static Class<?> writeParmMapToBaseType(Object o){
		return writeParmMapToBaseType.apply(o);
	}
	/**
	 * 获取包装类型的基本类型 netty 转解
	 * 如果需要 可以复写
	 * @param o
	 * @return
	 */
	public static Function<Object,Class<?>> writeParmMapToBaseType = (o)->{
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
	
	public byte[] read(DataTab tab, ByteBuf in){
		int readWriteIndex = 0;//readBytes
		try {
			Method[] m = typeToMethodInRW.get(tab.getForm());
			if (m != null && m[readWriteIndex] != null) {
				return  (byte[])m[readWriteIndex].invoke(in/*,*/);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 依据配置读内容
	 * @param tab
	 * @param in
	 * @return
	 */
	public byte[] readAndConvert(DataTab tab,DataType dataType ,ByteBuf in) {
		byte[] result = read(tab,in);
		if(result != null){
			Function<byte[], byte[]> fun = typeToBackConvert.get(new FunIndentity(dataType, tab.getId()));
			if (fun != null) {
				return fun.apply(result);
			}
			return result;
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

	public static Map<String, Method[]> getTypeToMethodInRW() {
		return typeToMethodInRW;
	}
	protected byte[] divide(byte[] bs,int i){
		int o = BaseTypeToBytesUtils.getInt(bs);
		return BaseTypeToBytesUtils.getBytes(o/i);
	}
	protected byte[] time(byte[] bs,int i){
		int o = BaseTypeToBytesUtils.getInt(bs);
		return BaseTypeToBytesUtils.getBytes(o/i);
	}
}
