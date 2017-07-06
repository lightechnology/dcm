package org.bdc.dcm.netty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.bdc.dcm.vo.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.ComParam;
import com.util.tools.Public;

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;

public class NettyBoot implements Runnable {

	final static Logger logger = LoggerFactory.getLogger(NettyBoot.class);

	private Server server;
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private ChannelFuture channelFuture;
	
	private Thread thread;
	private AtomicBoolean run;
	protected ExecutorService fixedThreadPool;

	public NettyBoot(Server server) {
		this.server = server;
		this.thread = new Thread(this);
		this.run = new AtomicBoolean(false);
	}

	public void startup() {
		run.set(true);
		thread.start();
	}

	public void shutdown() {
		run.set(false);
		if (null != channelFuture)
			channelFuture.channel().close();
	}

	@Override
	public void run() {
		while (run.get()) {
			try {
                task();
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage() + " info: " + server.toString(), e);
                } else {
                    Public.p(e.getMessage() + " info: " + server.toString());
                    e.printStackTrace();
                }
            }
			Public.sleepWithOutInterrupted(ComParam.HALFMIN);
		}
		finaled();
	}
	
	public void task() throws Exception{
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public EventLoopGroup getBossGroup() {
		return bossGroup;
	}

	public void setBossGroup(EventLoopGroup bossGroup) {
		this.bossGroup = bossGroup;
	}

	public EventLoopGroup getWorkerGroup() {
		return workerGroup;
	}

	public void setWorkerGroup(EventLoopGroup workerGroup) {
		this.workerGroup = workerGroup;
	}

	public ChannelFuture getChannelFuture() {
		return channelFuture;
	}

	public void setChannelFuture(ChannelFuture channelFuture) {
		this.channelFuture = channelFuture;
	}

	public boolean isInvalidHost() {
		Pattern pattern = Pattern
				.compile("^(?:(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))$");
		return null == server.getHost() || 0 == server.getHost().length()
				|| !pattern.matcher(server.getHost()).matches();
	}
	
    public void invalidHost() throws IllegalArgumentException {
        Pattern pattern = Pattern
                .compile("^(?:(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))$");
        if (null == server.getHost() || 0 == server.getHost().length()
                || !pattern.matcher(server.getHost()).matches()) {
            throw new IllegalArgumentException("invalid host!");
        }
    }
	
	public void finaled() {
		if (null != workerGroup && !workerGroup.isShuttingDown()
				&& !workerGroup.isShutdown())
			workerGroup.shutdownGracefully();
		if (null != bossGroup && !bossGroup.isShuttingDown()
				&& !bossGroup.isShutdown())
			bossGroup.shutdownGracefully();
	}
	
	public boolean isShutdown() {
		return !run.get();
	}
	
}
