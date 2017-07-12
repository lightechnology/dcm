package org.bdc.dcm.data.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.netty.lc.LcTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;

import com.util.tools.Public;

public class LcmdbEncoder implements DataEncoder<ByteBuf> {
	
	private final DataTabConf dataTabConf;

	public LcmdbEncoder() {
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	
	/**
	 * 	命令包 除了 modbus不同 和 命令包长度不同 其他一致
	 *  通过DataTypeConf接口获取编码规则
	 *	1.温度设置，开关机
	 *	2.继电器控制
	 *	3.远程锁定
	 */
	@Override
	public ByteBuf package2Data(ChannelHandlerContext ctx, DataPack msg) {
	  
		List<DataTab> dataTabs = IntfConf.getDataTabConf().getDataTabConf("Lc");
		
		ByteBuf src = ctx.alloc().buffer();
		
		Map<String, Object> data = msg.getData();
		
		Iterator<String> iterator = data.keySet().iterator();
		
		//取值
		String indentity = msg.getMac();
		
		byte[] indentityBytes = Public.hexString2bytes(indentity);
	
		//最后一位是modbus地址
		byte modbusAddr =indentityBytes[indentityBytes.length - 1];
		
		//真正的mac地址 (除去2位modbus地址 和 1个空格)
		String mac = indentity.substring(0, indentity.length() - 3 );
		byte[] reg = null,modbusPack = null;
		
		//用户控制命令 key迭代器
		while(iterator.hasNext()){
			///客户传递的key
			String reqKey = iterator.next();
			Optional<DataTab> optional = dataTabs.stream().filter(item->item.getId() == Integer.valueOf(reqKey)).findFirst();
			//在配置文件中
			if(!optional.isPresent()) break;
			//dataConf name=[form,value]
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>)data.get(reqKey);
			//第几路设置
			reg = intToByte4(Integer.valueOf(reqKey));
			//依据不同dataTab 发送不同的命令 
			DataTab tab = optional.get();
			LcTypeConvert.convertTypeStr2TypeId(tab.getForm());
			//依据字段类型得到输出的指令的modbus完整包
			modbusPack = LcTypeConvert.outModbusBytesByType(tab.getForm(),list.get(1),modbusAddr);
			//只取 第一个 
			break;
		}
		//找不到对应的控制命令解析器
		if(modbusPack == null ) {
			return null;
		};
		
		byte[] macBytes = Public.hexString2bytes(mac);
		int packLenOffset = 1;//包长度数组偏移量
		byte[] fixPackHeader = new byte[]{(byte)0xfe,(byte)0xA5};
		byte[] packHeader = new byte[]{01,00,(byte)0x16};//第2个字节就是包长度初始化为0
		byte packLen = Public.int2Bytes((1 + macBytes.length + 1 + modbusPack.length), 1)[0];
		packHeader[packLenOffset] = packLen;
		
		src.writeBytes(fixPackHeader);
		src.writeBytes(packHeader);
		src.writeBytes(macBytes);
		src.writeByte(0);
		src.writeBytes(modbusPack);
		int sum = 0;
		
		for(byte b:packHeader)
			sum+=b&0xff;
		
		for(byte b:macBytes)
			sum+=b&0xff;
		
		for(byte b:modbusPack)
			sum+=b&0xff;

		//crc校验和
		src.writeByte((byte)(sum & 0xff));
		return src;
	}
	//java 合并两个byte数组  
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
        return byte_3;  
    }  
    public static byte[] intToByte4(int i) {    
        byte[] targets = new byte[4];    
        targets[3] = (byte) (i & 0xFF);    
        targets[2] = (byte) (i >> 8 & 0xFF);    
        targets[1] = (byte) (i >> 16 & 0xFF);    
        targets[0] = (byte) (i >> 24 & 0xFF);    
        return targets;    
    }  
}
