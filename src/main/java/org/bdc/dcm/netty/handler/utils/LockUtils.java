package org.bdc.dcm.netty.handler.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class LockUtils {

	/**
	 * 通知
	 * @param lock
	 * @param condition
	 */
	public static void sign(Lock lock, Condition condition) {
		try {
			lock.lock();
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	/**
	 * 休眠
	 * @param lock
	 * @param condition
	 */
	public static void await(Lock lock, Condition condition,long sec) {
		try {
			lock.lock();
			if(sec > 0)
				condition.await(10,TimeUnit.SECONDS);
			else
				condition.await();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
	}
}
