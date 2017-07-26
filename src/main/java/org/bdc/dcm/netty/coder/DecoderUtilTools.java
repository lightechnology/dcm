package org.bdc.dcm.netty.coder;

import java.util.List;
import java.util.Set;

import org.bdc.dcm.data.coder.intf.DataDecoder;
import org.bdc.dcm.data.coder.intf.DatasDecoder;
import org.bdc.dcm.data.log.intf.Coder4Log;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.channel.ChannelManager;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.Datalevel;

import io.netty.channel.ChannelHandlerContext;

public class DecoderUtilTools<I> {
	
	private Coder4Log<I> coder4Log;
	private DataDecoder<I> decoder;
	private NettyBoot nettyBoot;
    private ChannelManager channelManager;
	
	public DecoderUtilTools(Coder4Log<I> coder4Log, DataDecoder<I> decoder, NettyBoot nettyBoot) {
		this.coder4Log = coder4Log;
		this.decoder = decoder;
		this.nettyBoot = nettyBoot;
		this.channelManager = ChannelManager.getInstance();
	}
	
	
	private boolean messageReceivedFilter(DataPack msg) {
		if (DataPackType.Cmd == msg.getDataPackType()) {
			Set<String> filterMacs = nettyBoot.getServer().getFilterMacs();
			// 无过滤条件，接口只接收状态数据和发送属于这个接口的命令数据
			if (null == filterMacs || !filterMacs.contains(msg.getToMac()) && !filterMacs.contains("0000000000000000")) {
				msg.setDatalevel(Datalevel.DELETED);
				return false;
			}
		}
		return true;
	}

	public void decode(ChannelHandlerContext ctx, I msg, List<Object> out) {
		
		if(decoder instanceof DatasDecoder) {
			List<DataPack> dataPacks = ((DatasDecoder<I>) decoder).datas2Package(ctx, msg);
			for(int i=0;i<dataPacks.size();i++) {
				long start1 = 0L, end1 = 0L, start2 = 0L, end2 = 0L, end3 = 0L;
				start1 = System.currentTimeMillis();
				DataPack dataPack = dataPacks.get(i);
				end1 = System.currentTimeMillis();
				if (null != dataPack && messageReceivedFilter(dataPack)) {
					dataPack.setTimestamp(start1);
					start2 = System.currentTimeMillis();
					coder4Log.log(msg, dataPack);
					end2 = System.currentTimeMillis();
					out.add(dataPack);
					end3 = System.currentTimeMillis();
					channelManager.codeEffWarnLog(start1, end1, start2, end2, end3, end3, 0);
				}
			}
		}else{
			long start1 = 0L, end1 = 0L, start2 = 0L, end2 = 0L, end3 = 0L;
			start1 = System.currentTimeMillis();
			DataPack dataPack = decoder.data2Package(ctx, msg);
			end1 = System.currentTimeMillis();
			if (null != dataPack && messageReceivedFilter(dataPack)) {
				dataPack.setTimestamp(start1);
				start2 = System.currentTimeMillis();
				coder4Log.log(msg, dataPack);
				end2 = System.currentTimeMillis();
				out.add(dataPack);
				end3 = System.currentTimeMillis();
				channelManager.codeEffWarnLog(start1, end1, start2, end2, end3, end3, 0);
			}
		}
		
	}
	
}
