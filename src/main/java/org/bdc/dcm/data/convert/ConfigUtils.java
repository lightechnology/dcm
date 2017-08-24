package org.bdc.dcm.data.convert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.util.tools.Public;


public class ConfigUtils {
	
	/**
	 * reverse("12345678",2) -> 78653412
	 * @param str
	 * @param len 需要截取保留不反序的字符串单元长度
	 * @return
	 */
	public static String reverse(String str,int len){
		StringBuffer sb = new StringBuffer();
		for(int i = str.length(); i > 0 ; i = i - len){
			String s = str.substring(i - len , i);
			sb.append(s);
			
		}
		return sb.toString();
	}
	
	/**
	 * 为num 加小数点 成字符串
	 * @param num
	 * @param index
	 * @return
	 */
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
	/**
	 * 
	 * @param bs
	 * @param startIndex
	 * @param endIndex 不包行这个末端下标的值
	 * @return
	 */
	public static int checkSum(byte[] bs ,int startIndex,int endIndex){
		int sum = 0;
		for(int i=startIndex;i<endIndex;i++){
			sum=(sum + bs[i])&0xff;
		}
		return sum;
	}
	
	/**
	 * 16进制数据 填充进 bytes
	 * @param hex
	 * @param bytes
	 * @return 填充完成后的下一个数组下标
	 */
	public static int hexStrToBytes(String hex,byte[] bytes,int startIndex){
		byte[] bs = Public.hexString2bytes(hex);
		int i = 0;
		for(;i<bs.length;i++)
			bytes[startIndex+i] = bs[i];
		return startIndex+i+1;
	}

	/**
	 * 依据输入流得到json 
	 * @param in
	 * @return
	 */
	public static String getJsonStr(InputStream in) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader isr = null;
        try {
        	isr = new InputStreamReader(in);
			BufferedReader bf = new BufferedReader(isr);
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        	if(isr != null)
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
     
        return stringBuilder.toString();
    }
}
