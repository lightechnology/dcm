package org.bdc.dcm.data.coder;

import static org.bdc.dcm.netty.lcmdb.LcmdbTypeConvert.*;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdc.dcm.conf.ComConf;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;
import io.netty.channel.ChannelHandlerContext;
public class LcmdbJsn1Encoder implements DataEncoder<String>  {

	final static Logger logger = LoggerFactory.getLogger(LcmdbJsn1Encoder.class);

	private NettyBoot nettyBoot;

	public LcmdbJsn1Encoder(NettyBoot nettyBoot) {
		this.nettyBoot = nettyBoot;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String package2Data(ChannelHandlerContext ctx, DataPack msg) {
		
		try {
			Map<String,Object> data = msg.getData();
			if(!data.isEmpty()) {
				Template json1 = ComConf.getInstance().getFmkTemp("jsonDataFormat01.ftl");
				StringWriter stringWriter = new StringWriter();
				BufferedWriter writer = new BufferedWriter(stringWriter);
				Map<String, Object> dataModel = new HashMap<String, Object>();
				dataModel.put("powerStatus", ((boolean)((List<Object>)data.get(DATATYPE_JDQSTATE+"")).get(1))?1:0);
				dataModel.put("senseTemperature", ((List<Object>)data.get(DATATYPE_TEMPERATURE+"")).get(1));
				dataModel.put("senseVoltage", ((List<Object>)data.get(DATATYPE_U+"")).get(1));
				dataModel.put("senseElectricity", ((List<Object>)data.get(DATATYPE_I+"")).get(1));
				dataModel.put("residueElectroTime", ((List<Object>)data.get(DATATYPE_REMAININGTIMELONG+"")).get(1));
				dataModel.put("residueEnergy", ((List<Object>)data.get(DATATYPE_REMAININGELECTRICITY+"")).get(1));
				dataModel.put("allElectroTime", ((List<Object>)data.get(DATATYPE_TOTALTIME+"")).get(1));
				dataModel.put("allEnergy", ((List<Object>)data.get(DATATYPE_TOTALACTIVEPOWER+"")).get(1));
				dataModel.put("power", ((List<Object>)data.get(DATATYPE_P+"")).get(1));
				dataModel.put("pcos", ((List<Object>)data.get(DATATYPE_COS$+"")).get(1));
				dataModel.put("maclist", Arrays.asList(new String[] {msg.getMac()}));

				
				json1.process(dataModel, writer);
				String reader = new String(stringWriter.toString());
				writer.flush();
				writer.close();
				
				
				return "json="+reader;
			}else
				return "json=";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "json=";
	}

}
