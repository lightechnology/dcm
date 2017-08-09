package org.bdc.dcm.data.coder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.netty.lcmdb.LcmdbTypeConvert;
import org.bdc.dcm.netty.ygdqmdb.YgdqmdbTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class YgdqmdbEncoder  implements DataEncoder<ByteBuf>  {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public ByteBuf package2Data(ChannelHandlerContext ctx, DataPack msg) {

		List<DataTab> dataTabs = IntfConf.getDataTabConf().getDataTabConf(DataType.Ygdqmdb.name());
		
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
			modbusPack = YgdqmdbTypeConvert.encoder(tab.getForm(),list.get(1),modbusAddr);
			//只取 第一个 
			break;
		}
		//找不到对应的控制命令解析器
		if(modbusPack == null ) {
			return src;
		};
		src.writeBytes(modbusPack);
		
		return src;
	}
}
