package org.bdc.dcm.netty.handler;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.netty.channel.ChannelManager;
import org.bdc.dcm.netty.handler.intfImpl.WriteQueueManage;
import org.bdc.dcm.netty.handler.intfImpl.WriteTask;
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
 * 
 * @Override 方法pollWrite可以实现自动读取通道数据功能，方法内容必须给w4prList赋值
 * @author adolp
 *
 */
public class DataHandler extends SimpleChannelInboundHandler<DataPack> implements Runnable {

    final static Logger logger = LoggerFactory.getLogger(DataHandler.class);
    
    public static final ExecutorService CACHED_THREAD_POOL = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            10L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());
    
    private NettyBoot nettyBoot;
    private ChannelManager channelManager;
    private InitSdata initSdata;
    private Monitor monitor;
    private PollReading pollReading;
    
    private List<Object> w4prList;
    
    private boolean run;
    private Lock lock;
    private Condition condition1;
    private Condition condition2;
    private Queue<Info> queue;
    private AtomicInteger deep;
   
    public DataHandler(NettyBoot nettyBoot) {
        this.nettyBoot = nettyBoot;
        this.channelManager = ChannelManager.getInstance();

        this.run = true;
        this.lock = new ReentrantLock();
        this.condition1 = lock.newCondition();
        this.condition2 = lock.newCondition();
        this.queue = new ConcurrentLinkedQueue<Info>();
        this.deep = new AtomicInteger(0);
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
            CACHED_THREAD_POOL.execute(initSdata);
        }
        //monitor = new Monitor();
        //CACHED_THREAD_POOL.execute(monitor);
        //CACHED_THREAD_POOL.execute(this);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        channelManager.rmvChannel(ctx);
        if (logger.isInfoEnabled()) {
            logger.info("remoteAddress: {} disconnected", ctx.channel().remoteAddress());
        }
        run = false;
        if (null != initSdata) {
            initSdata.stop();
            initSdata = null;
        }
        if (null != monitor) {
            monitor.stop();
            monitor = null;
        }
        if (null != pollReading) {
            pollReading.stop();
            pollReading = null;
        }
        logger.error("say byte");
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DataPack msg) throws Exception {
        if (null != initSdata) {
            initSdata.stop();
            initSdata = null;
            pollWrite(msg);
            if (null != w4prList && 0 < w4prList.size()) {
                pollReading = new PollReading(ctx);
                CACHED_THREAD_POOL.execute(pollReading);
            }
        } else {//内容分发
            signal();
            Long cur = System.currentTimeMillis();
            channelManager.setMaxInCost(cur - msg.getTimestamp());
            msg.setTimestamp(cur);
            channelManager.messagePublish(ctx, msg);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
       WriteQueueManage.Instance().addTask(new WriteTask(ctx, msg, promise)); 
    }

    @Override
    @Deprecated
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.close(ctx, promise);
    }

    public NettyBoot getNettyBoot() {
        return nettyBoot;
    }

    public void setNettyBoot(NettyBoot nettyBoot) {
        this.nettyBoot = nettyBoot;
    }

    /**
     * 初始参考数据往实现方法输出，可辅助生成w4prList
     * 
     * @param msg
     */
    public void pollWrite(DataPack msg) {
    }

    /**
     * 循环读取通道数据待发生的数据列表
     * 
     * @param w4prList
     */
    public void setW4prList(List<Object> w4prList) {
        this.w4prList = w4prList;
    }
    
    public List<Object> getW4prList() {
        return w4prList;
    }

    private DataPack buildHeartBeatMessage(ChannelHandlerContext ctx) {
        DataPack dataPack = new DataPack();
        dataPack.setDataPackType(DataPackType.HeartBeat);
        dataPack.setSocketAddress(ctx.channel().remoteAddress());
        dataPack.setTimestamp(System.currentTimeMillis());
        return dataPack;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                if (logger.isInfoEnabled()) {
                    logger.info("channel: {} - {} READER_IDLE", ctx.channel().localAddress(),
                            ctx.channel().remoteAddress());
                }
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                if (logger.isInfoEnabled()) {
                    logger.info("channel: {} - {} WRITER_IDLE", ctx.channel().localAddress(),
                            ctx.channel().remoteAddress());
                }
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                if (logger.isInfoEnabled()) {
                    logger.info("channel: {} - {} ALL_IDLE", ctx.channel().localAddress(),
                            ctx.channel().remoteAddress());
                }
            }
            // 发送心跳
            ctx.writeAndFlush(buildHeartBeatMessage(ctx));
        }
    }
    
    public void offer(final ChannelHandlerContext ctx, final Object msg) {
        Info info = new Info(ctx, msg);
        queue.offer(info);
        deep.getAndIncrement();
        try {
            lock.lock();
            condition1.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    public void signal() {
        try {
            lock.lock();
            condition2.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public void run() {
        while (run) {
            while (queue.isEmpty()) {
                try {
                    lock.lock();
                    condition1.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
            while (!queue.isEmpty()) {
                Info info = queue.poll();
                deep.getAndDecrement();
                ChannelHandlerContext ctx = info.ctx;
                Object msg = info.msg;
                if ( null != ctx && null != msg) {
                    if (run)
                        ctx.channel().writeAndFlush(msg);
                    else
                        break;
                    try {
                        lock.lock();
                        condition2.await(nettyBoot.getServer().getDelaySendingTime(), TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
    }

    class InitSdata implements Runnable {

        private boolean run;
        private final ChannelHandlerContext ctx;
        private String initSendingData;
        
        private int dst = 0;

        public InitSdata(final ChannelHandlerContext ctx, final String initSendingData) {
            this.run = true;
            this.ctx = ctx;
            this.initSendingData = initSendingData;
            this.dst = nettyBoot.getServer().getDelaySendingTime();
        }

        public void stop() {
            this.run = false;
        }

        @Override
        public void run() {
            while (run) {
                byte[] data = Public.hexString2bytes(initSendingData);
                ByteBuf src = ctx.alloc().buffer(data.length);
                src.writeBytes(data);
                // 这里在判断一次，细粒度执行
                if (run)
                    ctx.writeAndFlush(src);
                else
                    break;
                Public.sleepWithOutInterrupted(ComParam.ONESEC * dst);
            }
        }

    }
    
    class Monitor implements Runnable {

        final Logger logger = LoggerFactory.getLogger(this.getClass());

        private boolean run = true;

        public void stop() {
            this.run = false;
        }

        @Override
        public void run() {
            while (run) {
                long de = deep.get();
                if (logger.isWarnEnabled()) {
                    logger.warn("Write Queue is deep: {}", de);
                } else {
                    StringBuilder sb = new StringBuilder(
                            "Write Queue is deep: ").append(de);
                    Public.p(sb.toString());
                }
                Public.sleepWithOutInterrupted(ComParam.ONEMIN);
            }
        }
        
    }
    
    class PollReading implements Runnable {

        private boolean run;
        private ChannelHandlerContext ctx;

        private int dst = 0;

        public PollReading(final ChannelHandlerContext ctx) {
            this.run = true;
            this.ctx = ctx;
            this.dst = nettyBoot.getServer().getDelaySendingTime();
        }

        public void stop() {
            this.run = false;
        }

        @Override
        public void run() {
            while (run) {
                for (int i = 0; i < w4prList.size(); i++) {
                    Object msg = w4prList.get(i);
                    if (run)
                        ctx.channel().writeAndFlush(msg);
                    else
                        break;
                    Public.sleepWithOutInterrupted(ComParam.ONESEC * dst);
                }
            }
        }

    }
  
    class Info {

        public final ChannelHandlerContext ctx;
        public final Object msg;

        public Info(final ChannelHandlerContext ctx, final Object msg) {
            this.ctx = ctx;
            this.msg = msg;
        }
    }

}