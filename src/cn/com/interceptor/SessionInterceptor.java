package cn.com.interceptor;

import org.springframework.web.servlet.ModelAndView;
import cn.com.common.Const;
import cn.com.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author WangKai
 * @ClassName: SessionFilterUtil
 * @date 2019-08-21 18:17
 * @Description: ���ػ�ȡ�û���
 */
public class SessionInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		
		//��ӿ���CORS
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE,HEAD,PUT,PATCH");
        response.setHeader("Access-Control-Max-Age", "36000");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,Authorization,authorization");
        response.setHeader("Access-Control-Allow-Credentials","true");
		String url = request.getRequestURL().toString();
		// ����GIT�ֿ��ɹ���дһ��ע�Ͷ���
		// ��¼ҳ�治�ü�⣬��Ȼ�ٴ�ʹ����ת�����Cannot forward after response has been
		// committed����request����ύ��
		if (url.contains("login") || url.contains("js") || url.contains("img") || url.contains("css")
				|| url.contains("ico")) {
			return true;
		}
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(Const.LOGINNAME);
		if (user != null) {
			UserSession.set(Const.LOGINNAME, user.getName());
			UserSession.set("uuid", user.getId());
			return true;
		} else {
			/*
			 * �Զ��巵��
			response.setHeader("Content-Type", "application/json;charset=UTF-8");
			// û��Ȩ�޷���
			response.setCharacterEncoding("UTF-8");
			ServiceResp result =  ServiceResp.createByCodeErrorMessage(403, "û�з���Ȩ��");
			response.getWriter().write(JSON.toJSONString(result));
			*/
			response.setStatus(403);
			return false;
		}
		
	}


	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {

	}
}