package org.bdc.dcm.netty.channel;

import java.net.SocketAddress;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import org.bdc.dcm.conf.ComConf;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.NettyChannel;
import org.bdc.dcm.vo.e.DataPackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.ComParam;
import com.util.tools.Public;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.Attribute;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChannelManager implements Runnable {

    public final static Logger logger = LoggerFactory.getLogger(ChannelManager.class);

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private AtomicLong maxInCost;
    private AtomicLong maxOutCost;

    private Thread thread;

    private static final ChannelManager channelManager = new ChannelManager();

    private ChannelManager() {
        this.maxInCost = new AtomicLong(0);
        this.maxOutCost = new AtomicLong(0);

        this.thread = new Thread(this);
        this.thread.start();
    }

    public static ChannelManager getInstance() {
        return channelManager;
    }

    public void setChannel(ChannelHandlerContext ctx, SocketAddress socketAddress, String mac) {
        if (null != socketAddress) {
            Attribute<NettyChannel> attr = ctx.attr(ComConf.NETTY_CHANNEL_KEY);
            NettyChannel nChannel = attr.get();
            if (null == nChannel) {
                NettyChannel newNChannel = new NettyChannel();
                if (null != mac && 0 < mac.length())
                    newNChannel.addMac(mac);
                newNChannel.setSocketAddress(socketAddress);
                nChannel = attr.setIfAbsent(newNChannel);
            }
        }
        channelGroup.add(ctx.channel());
    }

    public void rmvChannel(ChannelHandlerContext ctx) {
        channelGroup.remove(ctx.channel());
    }

    private void initChannelAttr(final ChannelHandlerContext ctx, final DataPack dataPack) {
        // 给Channel加属性，遍历所有channel里面的mac，如果当前mac在里面找到，删，加到当前channel里
        NettyChannel nChannel = null;
        if (DataPackType.Info == dataPack.getDataPackType() && null != dataPack.getMac()
                && 0 < dataPack.getMac().length()) {
        	//lzh:网关不掉电的情况下，替换网关下设备通道依然是存在的所以此时必须把之前的mac剔除，也就是保持mac和channel的一一对应
            Iterator<Channel> iter = channelGroup.iterator();
            while (iter.hasNext()) {
                Channel ch = iter.next();
                if (!ch.id().asShortText().equals(ctx.channel().id().asShortText())) {
                    nChannel = ch.attr(ComConf.NETTY_CHANNEL_KEY).get();
                    if (null != nChannel) {
                        nChannel.removeMac(dataPack.getMac());
                    }
                }
            }
            Attribute<NettyChannel> attr = ctx.attr(ComConf.NETTY_CHANNEL_KEY);
            nChannel = attr.get();
            if (null == nChannel) {
                NettyChannel newNChannel = new NettyChannel();
                newNChannel.addMac(dataPack.getMac());
                newNChannel.setSocketAddress(dataPack.getSocketAddress());
                nChannel = attr.setIfAbsent(newNChannel);
                if (null != nChannel && nChannel != newNChannel) {
                    nChannel.addMac(dataPack.getMac());
                }
            } else {
                nChannel.addMac(dataPack.getMac());
            }
        }
    }

    private void publish(final ChannelHandlerContext ctx, final DataPack dataPack) {
        // 分发数据
        NettyChannel nChannel = null;
        boolean dptb = DataPackType.Cmd == dataPack.getDataPackType();
        Iterator<Channel> iter = channelGroup.iterator();
        while (iter.hasNext()) {
            Channel ch = iter.next();
            nChannel = ch.attr(ComConf.NETTY_CHANNEL_KEY).get();
            if (null != nChannel) {
                if (dptb && nChannel.getMacs().contains(dataPack.getToMac())
                        || !dptb && !ch.id().asShortText().equals(ctx.channel().id().asShortText())) {
                    if (ch.isWritable()) {
                    	//lzh:修正原本包记录的源地址，改为目标地址是当前从channelGroup找到的通道
                        dataPack.setSocketAddress(nChannel.getSocketAddress());
                        ch.writeAndFlush(dataPack);
                    } else {
                        if (logger.isInfoEnabled()) {
                            logger.info("Channel: {} is block, please waiting for write!",
                                    nChannel.getSocketAddress().toString());
                        }
                    }
                }
            }
        }
    }

    public void messagePublish(final ChannelHandlerContext ctx, final DataPack dataPack) {
        if (null != dataPack && null != ctx) {
            long start = System.currentTimeMillis();
            initChannelAttr(ctx, dataPack);
            long middle = System.currentTimeMillis();
            if (logger.isInfoEnabled()) {
                logger.info("message init channel attribute--------------------------start: {}, end: {}, all cost: {}",
                        start, middle, middle - start);
            }
            publish(ctx, dataPack);
            long end = System.currentTimeMillis();
            if (logger.isInfoEnabled()) {
                logger.info("message transmit--------------------------start: {}, end: {}, all cost: {}", middle, end,
                        end - middle);
            }
        }
    }

    public void codeEffWarnLog(long start1, long end1, long start2, long end2, long end3, long end4, int type) {
        final String[] tabNames0 = { "decode", "recode", "inpost", "driall" };
        final String[] tabNames1 = { "encode", "recode", "output", "eroall" };
        String[] tabNames = null;
        if (0 == type)
            tabNames = tabNames0;
        else
            tabNames = tabNames1;
        if (logger.isWarnEnabled()) {
            long xt1 = 0L, xt2 = 0L, xt3 = 0L, xt4 = 0L;
            xt1 = end1 - start1;
            xt2 = end2 - start2;
            xt3 = end3 - end2;
            xt4 = end4 - start1;
            StringBuilder sb = new StringBuilder();
            if (3 < xt1)
                sb.append("message ").append(tabNames[0]).append("--------------------------start: ").append(start1)
                        .append(", end: ").append(end1).append(", all cost: ").append(xt1);
            if (3 < xt2) {
                if (0 < sb.length())
                    sb.append("\n");
                sb.append("message ").append(tabNames[1]).append("--------------------------start: ").append(start2)
                        .append(", end: ").append(end2).append(", all cost: ").append(xt2);
            }
            if (3 < xt3) {
                if (0 < sb.length())
                    sb.append("\n");
                sb.append("message ").append(tabNames[2]).append("--------------------------start: ").append(end2)
                        .append(", end: ").append(end3).append(", all cost: ").append(xt3);
            }
            if (5 < xt4) {
                if (0 < sb.length())
                    sb.append("\n");
                sb.append("message ").append(tabNames[3]).append("--------------------------start: ").append(start1)
                        .append(", end: ").append(end4).append(", all cost: ").append(xt4);
            }
            if (0 < sb.length()) {
                logger.warn(sb.toString());
            }
        }
    }

    public void setMaxInCost(Long cost) {
        long maxCost = maxInCost.get();
        while (maxCost < cost && !maxInCost.compareAndSet(maxCost, cost))
            maxCost = maxInCost.get();
    }

    public void setMaxOutCost(Long cost) {
        long maxCost = maxOutCost.get();
        while (maxCost < cost && !maxOutCost.compareAndSet(maxCost, cost))
            maxCost = maxOutCost.get();
    }

//    public boolean channelIsWritable(Channel channel) {
//        Boolean attr = channel.attr(ComConf.NETTY_CHANNEL_WB).get();
//        if (null != attr)
//            return attr.booleanValue();
//        return true;
//    }
//
//    public void channelDisWrite(Channel channel) {
//        Attribute<Boolean> attr = channel.attr(ComConf.NETTY_CHANNEL_WB);
//        Boolean isWritable = attr.get();
//        if (null == isWritable) {
//            Boolean newIsWritable = false;
//            isWritable = attr.setIfAbsent(newIsWritable);
//            if (null != isWritable && isWritable != newIsWritable) {
//                isWritable = false;
//            }
//        } else {
//            isWritable = false;
//        }
//    }

    @Override
    public void run() {
        while (true) {
            long iu = maxInCost.getAndSet(0);
            long ou = maxOutCost.getAndSet(0);
            if (logger.isWarnEnabled()) {
                logger.warn("Indata time cost max: {}, Outdata time cost max: {}", iu, ou);
            } else {
                StringBuilder sb = new StringBuilder("Indata time cost max: ").append(iu)
                        .append(", Outdata time cost max: ").append(ou);
                Public.p(sb.toString());
            }
            /*
            StringBuilder sb = new StringBuilder();
            Iterator<Channel> iter = channelGroup.iterator();
            while (iter.hasNext()) {
                Channel ch = iter.next();
                NettyChannel nChannel = ch.attr(ComConf.NETTY_CHANNEL_KEY).get();  
                if (null != nChannel) {
                    sb.append(nChannel.getSocketAddress().toString()).append(" ");
                    Iterator<String> macs = nChannel.getMacs().iterator();
                    sb.append("macs:");
                    while (macs.hasNext()) {
                        sb.append(" ").append(macs.next());
                    }
                    sb.append("\n");
                }
            }
            logger.error(sb.toString());
            */
            Public.sleepWithOutInterrupted(ComParam.ONEMIN);
        }
    }
}
