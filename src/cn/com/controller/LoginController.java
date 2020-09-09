package cn.com.controller;

import cn.com.common.Const;
import cn.com.common.ServiceResp;
import cn.com.config.ConfigManager;
import cn.com.entity.User;
import cn.com.entity.dto.UserDto;
import cn.com.util.LogUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @descript : 登录 ，登出 和 下载相应的帮助文件
 *
 * @author : wangshuncheng
 * @date : Create in 16:07 2019/8/15
 */
@Controller
@RequestMapping("/login")
public class LoginController {

	/**
	 * @description ： 登录页面
	 * @param name * @param password * @param request * @param session
	 * @return java.lang.String
	 * @author wangshuncheng
	 * @date 2019/9/6 15:54
	 */
	@RequestMapping("/minor_login")
	@ResponseBody
	public ServiceResp login(@RequestBody UserDto postUser, HttpServletRequest request, HttpServletResponse response) {
		String action = "用户登录";
		String name = postUser.getName();
		String password = postUser.getPassword();
		if (name == null || name.equals("")) {
			LogUtil.logInfo(action, name, null, "用户名为空");
			return ServiceResp.createByErrorMessage("用户名为空");
			//return "redirect:" + Const.LOGIN_PAGE + "?error=name";
		}
		User user = ConfigManager.getUserMap().get(name);
		if (user == null) {
			LogUtil.logInfo(action, name, null, "不存在该用户");
			return ServiceResp.createByErrorMessage("不存在该用户");
			//return "redirect:" + Const.LOGIN_PAGE + "?error=user";
		}
		Digester md5 = new Digester(DigestAlgorithm.MD5);
		String pwd = md5.digestHex(name + password);
		if (pwd.equals(user.getPassword())) {
			HttpSession session = request.getSession();
			Integer level = user.getLevel();
			if (level != null && level >= 1 && level <= 4) {
				LogUtil.logInfo(action, name, null, "success");
				session.setAttribute(Const.LOGINNAME, user);
				return ServiceResp.createBySuccess(user);
				//return "redirect:" + Const.HOME_PAGE;
			} else {
				LogUtil.logFail(action, name, null, "未知的权限");
				return ServiceResp.createByErrorMessage("未知的权限");
				//return "redirect:" + Const.LOGIN_PAGE + "?error=level";
			}
		} else {
			LogUtil.logInfo(action, name, null, "密码错误");
			return ServiceResp.createByErrorMessage("密码错误");
			//return "redirect:" + Const.LOGIN_PAGE + "?error=password";
		}
	}

	/**
	 * @description 退出登录
	 * @return void
	 * @author wangshuncheng
	 * @date 2019/9/6 15:53
	 */
	@RequestMapping("/minor_logout")
	public ServiceResp logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute(Const.LOGINNAME);
		return ServiceResp.createBySuccess();
	}

	@RequestMapping("/query_login_stat")
	@ResponseBody
	public ServiceResp getLoginStat(HttpSession session) {
		User user = (User) session.getAttribute(Const.LOGINNAME);
		if (user != null) {
			return ServiceResp.createBySuccess(user);
		} else {
			return ServiceResp.createBySuccess(false);
		}
	}

}
