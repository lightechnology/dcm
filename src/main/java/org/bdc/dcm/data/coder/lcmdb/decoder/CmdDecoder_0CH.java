package org.bdc.dcm.data.coder.lcmdb.decoder;

import java.util.HashMap;

import org.bdc.dcm.data.coder.lcmdb.CommandTypeCtr;
import org.bdc.dcm.data.coder.lcmdb.utils.LcComonUtils;
import org.bdc.dcm.data.coder.lcmdb.vo.CommLcParam;
import org.bdc.dcm.vo.DataPack;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;

public class CmdDecoder_0CH implements CommandTypeCtr{

	@Override
	public DataPack mapTo(CommLcParam param) {
		ByteBuf in = param.getPack();
		DataPack dataPack = LcComonUtils.getInitDataPack("");
		int len = 8;
		if(in.readableBytes() == len){//验证通过
			byte[] by = new byte[len];
			in.readBytes(by);
			String mac = Public.byte2hex(by);
			dataPack = LcComonUtils.getInitDataPack(mac);
		}
		dataPack.setData(new HashMap<>());
		return dataPack;
	}

}
