package cn.com.util;

import cn.com.config.ConfigManager;
import cn.com.entity.DetectionTask;

public class TestThread extends Thread {
	
	private String taskNum;
	
	@Override
	public void run() {
		ConfigManager.getDetectionMap().get(this.taskNum).setState(DetectionTask.STATE_RUNNING);
		
		
		for(int i = 0; i < 60; i++ ) {
			if(Thread.currentThread().isInterrupted()) {
				ConfigManager.getDetectionMap().get(this.taskNum).setState(DetectionTask.STATE_READY);
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			int a = i;
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
