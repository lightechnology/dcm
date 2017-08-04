package org.bdc.dcm.data.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.netty.lcmdb.LcmdbTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

public class LcmdbEncoder implements DataEncoder<ByteBuf> {

	private Logger logger = LoggerFactory.getLogger(LcmdbEncoder.class);
	
	
	/**
	 * 	命令包 除了 modbus不同 和 命令包长度不同 其他一致
	 *  通过DataTypeConf接口获取编码规则
	 *	1.温度设置，开关机
	 *	2.继电器控制
	 *	3.远程锁定
	 */
	@Override
	public ByteBuf package2Data(ChannelHandlerContext ctx, DataPack msg) {
	  
		List<DataTab> dataTabs = IntfConf.getDataTabConf().getDataTabConf(DataType.Lcmdb.name());
		
		ByteBuf src = ctx.alloc().buffer();
		
		Map<String, Object> data = msg.getData();
		
		Iterator<String> iterator = data.keySet().iterator();
		
		DataPackType dataPackType = msg.getDataPackType();
		//取值
		String indentity = "";
		if(dataPackType.equals(DataPackType.Cmd)) 
			indentity = msg.getToMac(); 
		else if(dataPackType.equals(DataPackType.Info)) 
			indentity = msg.getMac();
		
		byte[] indentityBytes = Public.hexString2bytes(indentity);
	
		//最后一位是modbus地址
		byte modbusAddr =indentityBytes[indentityBytes.length - 1];
		
		//真正的mac地址 (除去2位modbus地址 和 1个空格)
		String mac = indentity.substring(0, indentity.length() - 3 );
		byte[] modbusPack = null;
		
		//用户控制命令 key迭代器
		while(iterator.hasNext()){
			///客户传递的key
			String reqKey = iterator.next();
			Optional<DataTab> optional = dataTabs.stream().filter(item->item.getId() == Integer.valueOf(reqKey)).findFirst();
			//在配置文件中
			if(!optional.isPresent()) break;
			//dataConf name=[form,value]
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) data.get(reqKey);
			//依据不同dataTab 发送不同的命令 
			DataTab tab = optional.get();
			//依据字段类型得到输出的指令的modbus完整包
			modbusPack = LcmdbTypeConvert.encoder(tab.getForm(),list.get(1),modbusAddr);
			//只取 第一个 
			break;
		}
		//找不到对应的控制命令解析器
		if(modbusPack == null ) {
			return src;
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
		
		for(int i=0;i<packHeader.length;i++) 
			sum+=packHeader[i]&0xff;
		for(int i=0;i<macBytes.length;i++)
			sum+=macBytes[i]&0xff;
		for(int i=0;i<modbusPack.length;i++)
			sum+=modbusPack[i]&0xff;

		//crc校验和
		src.writeByte((byte)(sum & 0xff));
		return src;
	}
	
}
