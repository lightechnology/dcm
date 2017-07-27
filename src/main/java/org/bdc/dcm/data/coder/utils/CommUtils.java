package org.bdc.dcm.data.coder.utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.data.coder.intf.TypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.Datalevel;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class CommUtils {

	public enum ModusResp{
		address,funcode,len
	}
	
	public static List<Object> makeMapValue(String name, Object value) {
		List<Object> vl = new ArrayList<Object>();
		vl.add(name);
		vl.add(value);
		return vl;
	}
	public static DataPack getInitInfoDataPack(String mac) {
		DataPack dataPack = new DataPack();
		dataPack.setMac(mac);
		dataPack.setOnlineStatus(1);
		dataPack.setDatalevel(Datalevel.NORMAL);
		dataPack.setDataPackType(DataPackType.Info);
		return dataPack;
	}
	public static DataPack getInitCmdDataPack(String toMac) {
		DataPack dataPack = new DataPack();
		dataPack.setToMac(toMac);
		dataPack.setOnlineStatus(1);
		dataPack.setDatalevel(Datalevel.NORMAL);
		dataPack.setDataPackType(DataPackType.Cmd);
		return dataPack;
	}
	public static String gernatorIndetity(byte[] macBytes,byte address){
		String mac = "";
		for(int i = 0;i < macBytes.length;i++)
			mac += Public.byte2hex_ex(macBytes[i]);
		mac += Public.byte2hex_ex(address);
		return mac;
	}
	public static List<Object> decodeValue(int id, ByteBuf value,List<DataTab> dataTabList,TypeConvert convert) {
		for (int i = 0; i < dataTabList.size(); i++) {
			DataTab dataTab = dataTabList.get(i);
			try {
				if (id == dataTab.getId()) {
					return makeMapValue(dataTab.getName(),convert.decode(dataTab.getForm(), value));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return makeMapValue("", null);
	}
	public static DataPack funCode_UnKnow(ByteBuf in, byte[] mac, byte addr) {
		byte[] bs = new byte[in.readableBytes()];
		in.readBytes(bs);
		DataPack dataPack = getInitInfoDataPack(Public.byte2hex(mac)+ " " +Public.byte2hex_ex(addr));
		dataPack.setData(new HashMap<>());
		return dataPack;
	}

	public static DataPack funCode_03(List<DataTab> dataTabList, ByteBuf in, byte[] mac, TypeConvert convert,
			byte addr) {
		int modbusDataLen = in.readByte()&0xff;//长度
		ByteBuf modbusDataBuf = in.readBytes(modbusDataLen);

		DataPack dataPack = getInitInfoDataPack(Public.byte2hex(mac) + " " +Public.byte2hex_ex(addr));
		Map<String, Object> data = new HashMap<String, Object>();
		for(int i=0;modbusDataBuf.isReadable();i++){
			List<Object> val = decodeValue(i, modbusDataBuf, dataTabList,convert);
			Object o = val.get(1);
			if(o != null)
				data.put(i + "", val);
		}
		
		dataPack.setData(data);
		in.readShort();//读完
		return dataPack;
	}
	public static DataPack modbusParse(List<DataTab> dataTabList, ByteBuf in, byte[] mac, TypeConvert convert) {
		byte addr = in.readByte();//地址
		byte funcode = in.readByte();
		if(funcode == (byte)0x03) {
			return funCode_03(dataTabList, in, mac, convert, addr);
		}else {
			return funCode_UnKnow(in, mac, addr);
		}
	}
	public static String byteToBit(byte b) {  
        return ""  
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)  
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)  
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)  
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);  
    } 
	public static void reverse(byte[] bs){
		byte tmp ;
		for(int i=0;i<bs.length/2;i++){
			tmp = bs[i];
			bs[i] = bs[bs.length - 1 -i];
			bs[bs.length - 1 - i] = tmp;
		}
	}
	public static byte[] intToByte4(int i) {    
        byte[] targets = new byte[4];    
        targets[3] = (byte) (i & 0xFF);    
        targets[2] = (byte) (i >> 8 & 0xFF);    
        targets[1] = (byte) (i >> 16 & 0xFF);    
        targets[0] = (byte) (i >> 24 & 0xFF);    
        return targets;    
    }  
	public static boolean verifyParamter(String jsonStr, String verifyCode,String key) {
		Date now = new Date();
		for (int i = -1; i < 2; i++) {
			String[] rs = md5Paramter(jsonStr, key, new Date(now.getTime() + i
					* 60000));
			if (verifyCode.equals(rs[1]))
				return true;
		}
		return false;
	}
	public static String[] md5Paramter(String jsonStr, String key, Date date) {
		String datetime = new SimpleDateFormat("yyyyMMddHHmm").format(date);
		datetime = datetime.substring(0, datetime.length() - 1) + "0";
		try {
			return new String[] { datetime,
					Public.stringToMD5(jsonStr + key + datetime) };
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				e.printStackTrace();
		}
		return null;
	}
	//java 合并两个byte数组  
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
        return byte_3;  
    }  
   
}
