package org.bdc.dcm.data.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.utils.LcmdbTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataType;
import org.bdc.dcm.vo.protocol.ModbusProtocol;

public class LcmdbEncoder implements DataEncoder<ByteBuf> {
	
	private final List<DataTab> dataTabList;

	private ModbusProtocol<LcmdbTypeConvert> modbusProtocol ;
	
	public LcmdbEncoder(DataType dataType) {
		this.dataTabList = IntfConf.getDataTabConf().getDataTabConf(dataType.name());
		modbusProtocol = new ModbusProtocol<>(new LcmdbTypeConvert() , dataTabList);
	}
	
	// 通过DataTypeConf接口获取编码规则
	@Override
	public ByteBuf package2Data(ChannelHandlerContext ctx, DataPack msg) {
		List<Integer> addrs = modbusProtocol.getRegAddrs();
		Map<String,Object> data = msg.getData();
		
		return null;
	}
	
}
