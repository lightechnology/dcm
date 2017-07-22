package org.bdc.dcm.netty.channel;

import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

	private Lock lock;
	private Condition condition;
	private Queue<Info> infoQueue;
	private AtomicInteger deep;
	private AtomicLong maxInCost;
	private AtomicLong maxOutCost;
	
	private Thread thread;
	private Thread monitorThread;

	private static final ChannelManager channelManager = new ChannelManager();

	private ChannelManager() {
		this.lock = new ReentrantLock();
		this.condition = lock.newCondition();
		this.infoQueue = new ConcurrentLinkedQueue<Info>();
		this.deep = new AtomicInteger(0);
		this.maxInCost = new AtomicLong(0);
		this.maxOutCost = new AtomicLong(0);
		
		this.thread = new Thread(this);
		this.monitorThread = new Thread(new Monitor());
		this.monitorThread.setDaemon(true);
		this.thread.start();
		this.monitorThread.start();
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

	@Override
	public void run() {
		while (true) {
			while (infoQueue.isEmpty())
				try {
					lock.lock();
					condition.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			while (!infoQueue.isEmpty()) {
				Info info = infoQueue.poll();
				DataPack msg = info.dataPack;
				ChannelHandlerContext ctx = info.ctx;
				deep.getAndDecrement();
				if (null != msg && null != ctx) {
                    long start = System.currentTimeMillis();
                    // 给Channel加属性，遍历所有channel里面的mac，如果当前mac在里面找到，删，加到当前channel里
			        NettyChannel nChannel = null;
			        if (DataPackType.Info == msg.getDataPackType() && null != msg.getMac() && 0 < msg.getMac().length()) {
			            Iterator<Channel> iter = channelGroup.iterator();
			            while (iter.hasNext()) {
			                Channel ch = iter.next();
			                if (!ch.id().asShortText().equals(ctx.channel().id().asShortText())) {
			                    nChannel = ch.attr(ComConf.NETTY_CHANNEL_KEY).get();  
			                    if (null != nChannel) {
			                        nChannel.removeMac(msg.getMac());
			                    }
			                }
			            }
			            Attribute<NettyChannel> attr = ctx.attr(ComConf.NETTY_CHANNEL_KEY);
			            nChannel = attr.get();  
			            if (null == nChannel) {
			                NettyChannel newNChannel = new NettyChannel();
			                newNChannel.addMac(msg.getMac());
			                newNChannel.setSocketAddress(msg.getSocketAddress());
			                nChannel = attr.setIfAbsent(newNChannel);
			                if (null != nChannel && nChannel != newNChannel) {
			                    nChannel.addMac(msg.getMac());
			                }
			            } else {
			                nChannel.addMac(msg.getMac());
			            }
			        }
                    long middle = System.currentTimeMillis();
                    if (logger.isInfoEnabled()) {
                        logger.info("message init channel attribute--------------------------start: {}, end: {}, all cost: {}", start, middle, middle - start);
                    }
			        // 分发数据
			        boolean dptb = DataPackType.Cmd == msg.getDataPackType();
			        Iterator<Channel> iter = channelGroup.iterator();
			        while (iter.hasNext()) {
			            Channel ch = iter.next();
			            nChannel = ch.attr(ComConf.NETTY_CHANNEL_KEY).get();
			            if (null != nChannel) {
			                if (dptb && nChannel.getMacs().contains(msg.getToMac())
			                        || !dptb && !ch.id().asShortText().equals(ctx.channel().id().asShortText())) {
			                    if (ch.isWritable()) {
			                        msg.setSocketAddress(nChannel.getSocketAddress());
			                        ch.writeAndFlush(msg);
			                    } else {
			                        if (logger.isInfoEnabled()) {
			                            logger.info("Channel: {} is block, please waiting for write!", nChannel.getSocketAddress().toString());
			                        }
			                    }
			                }
			            }
			        }
                    long end = System.currentTimeMillis();
                    if (logger.isInfoEnabled()) {
                        logger.info("message transmit--------------------------start: {}, end: {}, all cost: {}", middle, end, end - middle);
                    }
				}
			}
		}
	}
	
	public void messagePublish(final ChannelHandlerContext ctx, final DataPack dataPack) {
	    Info info = new Info(ctx, dataPack);
		infoQueue.offer(info);
		deep.getAndIncrement();
		try {
			lock.lock();
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public void codeEffWarnLog(long start1, long end1, long start2, long end2, long end3, long end4, int type) {
	    final String[] tabNames0 = {"decode", "recode", "inpost", "driall"};
	    final String[] tabNames1 = {"encode", "recode", "output", "eroall"};
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
                sb.append("message ").append(tabNames[0]).append("--------------------------start: ").append(start1).append(", end: ").append(end1).append(", all cost: ").append(xt1);
            if (3 < xt2) {
                if (0 < sb.length()) sb.append("\n");
                sb.append("message ").append(tabNames[1]).append("--------------------------start: ").append(start2).append(", end: ").append(end2).append(", all cost: ").append(xt2);
            }
            if (3 < xt3) {
                if (0 < sb.length()) sb.append("\n");
                sb.append("message ").append(tabNames[2]).append("--------------------------start: ").append(end2).append(", end: ").append(end3).append(", all cost: ").append(xt3);
            }
            if (5 < xt4) {
                if (0 < sb.length()) sb.append("\n");
                sb.append("message ").append(tabNames[3]).append("--------------------------start: ").append(start1).append(", end: ").append(end4).append(", all cost: ").append(xt4);
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

	class Monitor implements Runnable {
		
		final Logger logger = LoggerFactory.getLogger(this.getClass());

		@Override
		public void run() {
			while (true) {
			    long de = deep.get();
				long iu = maxInCost.getAndSet(0);
				long ou = maxOutCost.getAndSet(0);
				if (logger.isWarnEnabled()) {
				    logger.warn("Data transmit Queue is deep: {}, indata time cost max: {}, outdata time cost max: {}", de, iu, ou);
				} else {
					StringBuilder sb = new StringBuilder(
							"Data transmit Queue is deep: ").append(de)
							.append(", indata time cost max: ").append(iu)
							.append(", outdata time cost max: ").append(ou);
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
	
	class Info {
	    
	    public final DataPack dataPack;
	    public final ChannelHandlerContext ctx;
	    
	    public Info (final ChannelHandlerContext ctx, final DataPack dataPack) {
	        this.ctx = ctx;
	        this.dataPack = dataPack;
	    }
	}
	
}
