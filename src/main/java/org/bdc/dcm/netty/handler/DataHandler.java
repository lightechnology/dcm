package org.bdc.dcm.netty.handler;

import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.channel.ChannelManager;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.e.DataPackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.ComParam;
import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 主要处理解析好的数据是否可接收和转发，还有就时记录数据来源和接口关系，以保证下发数据能找到正确的接口
 */
public class DataHandler extends SimpleChannelInboundHandler<DataPack> {
	
	final static Logger logger = LoggerFactory.getLogger(DataHandler.class);
	
	private NettyBoot nettyBoot;
	private ChannelManager channelManager;
	private InitSdata initSdata;
	
	public DataHandler(NettyBoot nettyBoot) {
		this.nettyBoot = nettyBoot;
        this.channelManager = ChannelManager.getInstance();
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		channelManager.setChannel(ctx, ctx.channel().remoteAddress(), nettyBoot.getServer().getSelfMac());
		if (logger.isInfoEnabled()) {
		    logger.info("remoteAddress: {} connected", ctx.channel().remoteAddress());
		}
		String initSendingData = nettyBoot.getServer().getInitSendingData();
        if (null != initSendingData && 0 < initSendingData.length()) {
            initSdata = new InitSdata(ctx, initSendingData);
            Thread thread = new Thread(initSdata);
            thread.start();
        }
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
        channelManager.rmvChannel(ctx);
		if (logger.isInfoEnabled()) {
		    logger.info("remoteAddress: {} disconnected", ctx.channel().remoteAddress());
		}
	}

    @Override
	protected void messageReceived(ChannelHandlerContext ctx, DataPack msg)
			throws Exception {
	    if (null != initSdata) {
	        initSdata.setRun(false);
	        initSdata = null;
	        pollReading(msg);
	    } else {
            Long cur = System.currentTimeMillis();
            channelManager.setMaxInCost(cur - msg.getTimestamp());
            msg.setTimestamp(cur);
            channelManager.messagePublish(ctx, msg);
	    }
	}

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("write: {} ", msg.toString());
        }
        super.write(ctx, msg, promise);
    }

	public NettyBoot getNettyBoot() {
		return nettyBoot;
	}

	public void setNettyBoot(NettyBoot nettyBoot) {
		this.nettyBoot = nettyBoot;
	}

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                if (logger.isInfoEnabled()) {
                    logger.info("channel: {} - {} READER_IDLE", ctx.channel().localAddress(), ctx.channel().remoteAddress());
                }
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                if (logger.isInfoEnabled()) {
                    logger.info("channel: {} - {} WRITER_IDLE", ctx.channel().localAddress(), ctx.channel().remoteAddress());
                }
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                if (logger.isInfoEnabled()) {
                    logger.info("channel: {} - {} ALL_IDLE", ctx.channel().localAddress(), ctx.channel().remoteAddress());
                }
            }
            // 发送心跳
            ctx.writeAndFlush(buildHeartBeatMessage(ctx));
        }
    }
    
    private DataPack buildHeartBeatMessage(ChannelHandlerContext ctx) {
        DataPack dataPack = new DataPack();
        dataPack.setDataPackType(DataPackType.HeartBeat);
        dataPack.setSocketAddress(ctx.channel().remoteAddress());
        dataPack.setTimestamp(System.currentTimeMillis());
        return dataPack;
    }
	
    class InitSdata implements Runnable {
        
        private boolean run;
        private final ChannelHandlerContext ctx;
        private String initSendingData;
        
        public InitSdata(final ChannelHandlerContext ctx, final String initSendingData) {
            this.run = true;
            this.ctx = ctx;
            this.initSendingData = initSendingData;
        }

        public void setRun(boolean run) {
            this.run = run;
        }

        @Override
        public void run() {
            while (run) {
                int lpil = nettyBoot.getServer().getLoopInitSendingDataInterval();
                byte[] data = Public.hexString2bytes(initSendingData);
                ByteBuf src = ctx.alloc().buffer(data.length);
                src.writeBytes(data);
                ctx.writeAndFlush(src);
                Public.sleepWithOutInterrupted(ComParam.ONESEC * lpil);
            }
        }
        
    }
    
    public void pollReading(DataPack msg) {
    }
}