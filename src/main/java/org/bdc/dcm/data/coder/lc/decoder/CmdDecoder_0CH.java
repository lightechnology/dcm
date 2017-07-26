package org.bdc.dcm.data.coder.lc.decoder;

import java.util.HashMap;

import org.bdc.dcm.data.coder.lc.CommandTypeCtr;
import org.bdc.dcm.data.coder.lc.util.LcComonUtils;
import org.bdc.dcm.data.coder.lc.vo.CommLcParam;
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
