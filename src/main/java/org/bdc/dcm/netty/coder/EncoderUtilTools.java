package org.bdc.dcm.netty.coder;

import java.util.List;
import java.util.Set;

import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.data.log.intf.Coder4Log;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.channel.ChannelManager;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataPackType;
import org.bdc.dcm.vo.e.Datalevel;

import io.netty.channel.ChannelHandlerContext;

public class EncoderUtilTools<I> {
	
	private Coder4Log<I> coder4Log;
	private DataEncoder<I> encoder;
	private NettyBoot nettyBoot;
	
	public EncoderUtilTools(Coder4Log<I> coder4Log, DataEncoder<I> encoder, NettyBoot nettyBoot) {
		this.coder4Log = coder4Log;
		this.encoder = encoder;
		this.nettyBoot = nettyBoot;
	}

	private boolean messageSendingFilter(DataPack msg) {
		if (DataPackType.Info == msg.getDataPackType()) {
			Set<String> filterMacs = nettyBoot.getServer().getFilterMacs();
			// 无过滤条件，接口只接收状态数据和发送属于这个接口的命令数据
			if (null == filterMacs || !filterMacs.contains(msg.getMac()) && !filterMacs.contains("0000000000000000")) {
				msg.setDatalevel(Datalevel.DELETED);
				return false;
			}
		}
		return true;
	}

	public void encode(ChannelHandlerContext ctx, DataPack msg, List<Object> out) {
		long start1 = 0L, end1 = 0L, start2 = 0L, end2 = 0L, end3 = 0L, end4 = 0L;
		if (messageSendingFilter(msg)) {
			start1 = System.currentTimeMillis();
			I cast = encoder.package2Data(ctx, msg);
			end1 = System.currentTimeMillis();
			if (null != cast) {
				start2 = System.currentTimeMillis();
				coder4Log.log(cast, msg);
				end2 = System.currentTimeMillis();
				out.add(cast);
				end3 = System.currentTimeMillis();
				ChannelManager channelManager = ChannelManager.getInstance();
				channelManager.setMaxOutCost(System.currentTimeMillis() - msg.getTimestamp());
				end4 = System.currentTimeMillis();
	            channelManager.codeEffWarnLog(start1, end1, start2, end2, end3, end4, 1);
			}
		}
	}
	
}
