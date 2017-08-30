package org.bdc.dcm.data.coder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import static org.bdc.dcm.data.coder.utils.CommUtils.*;
public class LqmdbEncoder implements DataEncoder<ByteBuf>  {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param ctx
	 * @param msg 用户传的数据
	 */
	@Override
	public ByteBuf package2Data(ChannelHandlerContext ctx, DataPack msg) {
		
		ByteBuf src = ctx.alloc().buffer(8);
		
		List<DataTab> dataTabs = IntfConf.getDataTabConf().getDataTabConf(DataType.Lqmdb.name());
		
		
		DataPackType dataPackType = msg.getDataPackType();
		String indentity = "";
		if(dataPackType.equals(DataPackType.Cmd)) 
			indentity = msg.getToMac(); 
		else if(dataPackType.equals(DataPackType.Info)) 
			indentity = msg.getMac();
		
		byte[] macBytes = Public.hexString2bytes(indentity);
		
		Map<String, Object> data = msg.getData();
		byte addr = macBytes[macBytes.length - 1];
		
		//编码只是用来写 读直接用bytebuf透传
		byte funCode = 0x06;
		
		byte[] reg = null,regVal = null ;
		
		Iterator<String> iterator = data.keySet().iterator();
		while(iterator.hasNext()){
			//客户传递的key
			String reqKey = iterator.next();
			Optional<DataTab> optional = dataTabs.stream().filter(item->item.getId() == Integer.valueOf(reqKey)).findFirst();
			//在配置文件中
			if(!optional.isPresent()) break;
			//dataConf name=[form,value]
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>)data.get(reqKey);
			reg = intToByte4(Integer.valueOf(reqKey));
			//取值
			regVal = (boolean)list.get(1)?new byte[]{00,01}:new byte[]{00,00};
			//只取 第一个 
			break;
		}
		if( reg == null || regVal == null ) {
			logger.info("寄存器：{},值：{}",reg,regVal);
			src.clear();
			return src;
		}
		
		byte reAddrH = reg[2]; 
		byte reAddrL = reg[3];
		byte reValH = regVal[0];
		byte reValL = regVal[1];
		
		//这里反序了
		byte[] crc16 = Public.crc16_A001(new byte[]{addr,funCode,reAddrH,reAddrL,reValH,reValL});
		if(logger.isInfoEnabled())
			logger.debug("cmd：{} {} {} {} {} {} {} {}",
					Public.byte2hex_ex(addr),
					Public.byte2hex_ex(funCode),
					Public.byte2hex_ex(reAddrH),
					Public.byte2hex_ex(reAddrL),
					Public.byte2hex_ex(reValH),
					Public.byte2hex_ex(reValL),
					Public.byte2hex_ex(crc16[1]),
					Public.byte2hex_ex(crc16[0])
				);
		
		
		
		src.writeByte(addr);
		src.writeByte(funCode);
		src.writeByte(reAddrH);//寄存器高地址
		src.writeByte(reAddrL);//寄存器低地址
		src.writeByte(reValH);//寄存器高值
		src.writeByte(reValL);//寄存器低值
		src.writeByte(crc16[1]);//crc 
		src.writeByte(crc16[0]);//crc
		return src;
	}
	
}
