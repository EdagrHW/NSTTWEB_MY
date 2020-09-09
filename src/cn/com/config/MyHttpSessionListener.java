package cn.com.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import cn.com.common.Const;
import cn.com.entity.User;
import cn.com.interceptor.UserSession;
import cn.com.util.LogUtil;


public class MyHttpSessionListener implements HttpSessionListener {
	// ʵ��List�̰߳�ȫ
	private  static List<HttpSession> sessionList = Collections.synchronizedList(new ArrayList<>());
	
	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		LogUtil.logInfo("����session", null, null, null);
		HttpSession session = httpSessionEvent.getSession();
        sessionList.add(session);
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		LogUtil.logInfo("����session", null, null, null);
		HttpSession session = httpSessionEvent.getSession();
        sessionList.remove(session);
	}
	
	public static void destorySession(String name){
	    Iterator<HttpSession> iterator = sessionList.iterator();
	    LogUtil.logInfo("�����û�", UserSession.get(Const.LOGINNAME), name, null);
	    while (iterator.hasNext()){
	        HttpSession session = iterator.next();
	        if(session!=null){
	            User user = (User)session.getAttribute(Const.LOGINNAME);
	            if(user!=null && (user.getName().equals(name))){
	                session.removeAttribute(Const.LOGINNAME);
	            }
	        }
	    }
	}
}
