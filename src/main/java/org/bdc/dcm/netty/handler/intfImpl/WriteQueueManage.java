package org.bdc.dcm.netty.handler.intfImpl;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bdc.dcm.netty.handler.DataHandler;
import org.bdc.dcm.netty.handler.intf.WriteTaskIntf;
import org.bdc.dcm.netty.handler.utils.LockUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.print.resources.serviceui;

/**
 * 随着队列的增多 当到达一个量级之后 必须新增一个线程去处理 防止阻塞
 * @author Administrator
 *
 */
public class WriteQueueManage{
	
	private static Logger logger = LoggerFactory.getLogger(WriteQueueManage.class);
	
	//防止操作的时候需要遍历
	private static Map<String,WriteThread> wMap = new Hashtable<>();
	
	static{
		WriteThread thread = new WriteThread();
		thread.notDel = true;
		execute(thread);
	}
	
	private static WriteQueueManage writeQueueManage = new WriteQueueManage();
	
	public static WriteQueueManage Instance(){
		return writeQueueManage;
	}
	public void remove(String key){
		wMap.remove(key);
	}
	public int size(){
		return wMap.size();
	}
	public void addTask(WriteTaskIntf write) {
		Optional<WriteThread> optional = wMap.values().stream().filter(item->item.size() < 300).findFirst();
		WriteThread writeThread = null;
		if(!optional.isPresent()){
			writeThread = new WriteThread();
			execute(writeThread);
		}else
			writeThread = optional.get();
		
		writeThread.addTask(write);
	}

	private static void execute(WriteThread writeThread) {
		wMap.put(writeThread.id, writeThread);
		DataHandler.CACHED_THREAD_POOL.execute(writeThread);
		logger.error("~~~添加一个新的写线程~~~：{}",writeThread.id);
	}
}
/**
 * 
 * @author 李哲弘
 *
 */
class WriteThread implements Runnable{

	/**
	 * 当前线程能写的队列
	 */
	private Queue<WriteTaskIntf> queue = new ConcurrentLinkedQueue<>();
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Lock lock;
	private Condition condition;
	private boolean isRun ;
	public String id;
	public boolean notDel;
	
	public WriteThread(){
		this.id = UUID.randomUUID().toString();
		this.lock = new ReentrantLock();
		this.condition = lock.newCondition();
		isRun = true;
	}
	public void addTask(WriteTaskIntf write) {
		queue.offer(write);//先加任务 再唤醒
		LockUtils.sign(lock, condition);
	}

	public int size() {
		return queue.size();
	}

	@Override
	public void run() {
		while(isRun){
			while(isRun && queue.isEmpty()){
				LockUtils.await(lock, condition, 10);
				if(queue.isEmpty() && !notDel){
					isRun = false;
				}else{
					int size = WriteQueueManage.Instance().size();
					if(notDel)
						logger.error("当前管理共多少个写线程：{},当前写队列个数:{}",size,queue.size(),isRun);
				}
			}
			while(isRun && !queue.isEmpty()){
				try {
					WriteTaskIntf intf = queue.poll();
					intf.invoke();
					intf = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		if(!notDel){//保证不需要删除的不删除
			WriteQueueManage manage = WriteQueueManage.Instance();
			manage.remove(id);
		}
	}
	
}