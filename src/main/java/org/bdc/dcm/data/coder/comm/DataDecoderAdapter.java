package org.bdc.dcm.data.coder.comm;

import java.util.ArrayList;
import java.util.List;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.utils.CommTypeConvert;
import org.bdc.dcm.utils.DataTabUtils;
import org.bdc.dcm.vo.DataModel;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public abstract class DataDecoderAdapter implements DataDecoder<ByteBuf> {

	/**
	 * 寄存器表
	 */
	private List<DataTab> regTable;
	/**
	 * 协议模型配置
	 */
	private List<DataTab> packConf = new ArrayList<>();
	
	private CommTypeConvert convert ;
	
	public DataDecoderAdapter(DataType dataType) {
		try {
			this.regTable = IntfConf.getDataTabConf().getDataTabConf(dataType.name());
			this.convert = (CommTypeConvert)Class.forName("org.bdc.dcm.utils."+dataType.name()+"TypeConvert").newInstance();
			packConf = DataTabUtils.initDataTabConf(dataType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<DataTab> getRegTable() {
		return regTable;
	}

	// 通过DataTypeConf接口获取解码规则
	@Override
	public DataPack data2Package(ChannelHandlerContext ctx, ByteBuf msg) {
		List<DataModel> dataModelList = new ArrayList<>();
		//整包配置
		for(DataTab tab:packConf){
			if(!CommTypeConvert.getTypeToMethodInRW().containsKey(tab.getForm())){
				customProtocol(ctx,msg);
			}else{
				Object o = convert.read(tab, msg);
				DataModel dataModel = (DataModel) tab;
				dataModel.setVal(o);
				dataModelList.add(dataModel);
			}
		}
		knownProtocol(ctx,dataModelList);
		return null;
	}
	/**
	 * 
	 * @param ctx
	 * @param msg 解析不出的byteBuf
	 */
	public abstract void customProtocol(ChannelHandlerContext ctx, ByteBuf msg);
	/**
	 * 
	 * @param ctx
	 * @param dataModelList 被接触出来的东西
	 */
	public abstract void knownProtocol(ChannelHandlerContext ctx, List<DataModel> dataModelList);
}
