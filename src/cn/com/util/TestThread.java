package cn.com.util;

import cn.com.config.ConfigManager;
import cn.com.entity.DetectionTask;

public class TestThread extends Thread {
	
	private String taskNum;
	
	@Override
	public void run() {
		ConfigManager.getDetectionMap().get(this.taskNum).setState(DetectionTask.STATE_RUNNING);
		
		for(long i = 0; i < 100000000000L; i++ ) {
			if(Thread.currentThread().isInterrupted()) {
				ConfigManager.getDetectionMap().get(this.taskNum).setState(DetectionTask.STATE_READY);
				break;
			}
			long a = i;
			a++;
			System.out.println(i);
		}
		ConfigManager.getDetectionMap().get(this.taskNum).setState(DetectionTask.STATE_READY);
	}

	public TestThread(String taskNum) {
		super();
		this.taskNum = taskNum;
	}

	public TestThread() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
