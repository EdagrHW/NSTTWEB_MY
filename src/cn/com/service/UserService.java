package cn.com.service;

import cn.com.common.RoleEnum;
import cn.com.common.ServiceResp;
import cn.com.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @descript :  角色管理接口
 *
 * @author : wangshuncheng
 * @date : Create in 16:07 2019/8/15
 */
public interface UserService {

         ServiceResp addUser(User user);

         ServiceResp modifyUser(String name);

         ServiceResp deleteUser(String name);

         ServiceResp modifyPassword(String name,String newPwd,String oldPwd);
         
         // 通过用户级别查询路由列表
         ServiceResp getRouters(Integer level);

         void downloadHelpBook(HttpSession session, HttpServletRequest request, HttpServletResponse response);
}
