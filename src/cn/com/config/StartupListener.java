package cn.com.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @description
 *     启动监听器用来加载配置文件
 * @author zhaoxin
 * @date 2019/8/13 13:59
 */
@Component("StartupListener")
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

	/**
	 * @description
	 *     ContextStartedEvent  容器启动调用的事件
	 *     ContextRefreshedEvent
	 *     ContextStoppedEvent
	 *     ContextClosedEvent
	 *     RequestHandleEvent
	 * @author zhaoxin
	 * @date 2019/8/13 14:23
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
			ConfigManager.loadConfig();
	}

}
