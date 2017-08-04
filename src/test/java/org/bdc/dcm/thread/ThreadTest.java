package org.bdc.dcm.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadTest {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Test
	public void test(){
		try {
			List<RunTask> runnables = new ArrayList<>();
			System.out.println("开始~~active:"+Thread.activeCount());
			ExecutorService executorService = Executors.newCachedThreadPool();
			for(int i=0;i<1000;i++)
				runnables.add(new RunTask(i+""));
			for(int i=0;i<200;i++){
				executorService.submit(runnables.get(i));
				
			}
			Thread.sleep(1000*10);
			System.out.println("添加完毕~~active:"+Thread.activeCount());
			for(int i=0;i<200;i++){
				runnables.get(i).stop();
			}
			Thread.sleep(1000*10);
			System.out.println("主动关闭~~active:"+Thread.activeCount());
			Thread.sleep(1000*60*2);
			System.out.println("全部结束~~active:"+Thread.activeCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
class RunTask implements Runnable{

	private String name;
	
	private boolean isStop;

	RunTask(String name){
		this.name = name;
	}
	
	public void stop() {
		this.isStop = false;
	}

	@Override
	public void run() {
		while(isStop){
			//System.out.println(name);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}