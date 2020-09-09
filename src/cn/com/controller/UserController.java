package cn.com.controller;

import cn.com.aop.LevelRole;
import cn.com.common.Const;
import cn.com.common.RoleEnum;
import cn.com.config.ConfigManager;
import cn.com.entity.User;
import cn.com.common.ServiceResp;
import cn.com.interceptor.UserSession;
import cn.com.service.UserService;
import cn.com.util.LogUtil;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @descript :  角色管理
 *
 * @author : wangshuncheng
 * @date : Create in 16:07 2019/8/15
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static TimedCache timedCache = CacheUtil.newTimedCache(30*60*1000);

    @Autowired
    private UserService userService;



    /**
     * @description  添加用户
     * @param user   新添加的用户的信息
   * @param result
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/6 16:02
     */
    @RequestMapping("/add_user")
    @ResponseBody
    @LevelRole(RoleEnum.SYSADMIN)
    public ServiceResp addUser(@RequestBody @Validated User user, BindingResult result){
        String action = "添加用户";
        if(result.hasErrors()){
            StringBuffer buf = new StringBuffer();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for(FieldError error:fieldErrors){
               buf.append(error.getDefaultMessage()+";");
            }
            LogUtil.logFail(action, UserSession.get(Const.LOGINNAME),null,"fail");
            return ServiceResp.createByErrorMessage(buf.toString());
        }
        return userService.addUser(user);

    }

    /**
     * @description
     * @param name  需要重置口令的用户名
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/6 16:03
     */
    @RequestMapping("/modify_user")
    @ResponseBody
    @LevelRole(RoleEnum.SYSADMIN)
    public ServiceResp changePassword(@NotBlank String name){
        return userService.modifyUser(name);

    }

    /**
     * @description  删除用户
     * @param name   需要删除的用户名
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/6 16:03
     */
    @RequestMapping("/delete_user")
    @ResponseBody
    @LevelRole(RoleEnum.SYSADMIN)
    public ServiceResp deleteUser(@NotBlank String name, HttpSession session){
        Object obj = session==null?null:session.getAttribute(Const.LOGINNAME);
        User user = (User)obj;
        if(user!=null){
            if(user.getName().equals(name)){
                return ServiceResp.createByErrorMessage("不能删除当前你正在登录的用户");
            }
        }
        return userService.deleteUser(name);

    }

    /**
     * @description  查询用户路由列表
     * @return cn.com.infosec.common.ServiceResp
     * @author huangwei
     * @date 2020/09/03 16:04
     */
    @RequestMapping("/query_router")
    @ResponseBody
    public ServiceResp getRouters(Integer level){
    	if(level == null) {
    		return ServiceResp.createByErrorMessage("查询路由参数失败，未传入用户权限等级");
    	}
        return userService.getRouters(level);
    }
    
    /**
     * @description  查询所有用户
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/6 16:04
     */
    @RequestMapping("/query_user")
    @ResponseBody
    @LevelRole(RoleEnum.SYSADMIN)
    public ServiceResp getUsers(){
        Collection<User> values = ConfigManager.getUserMap().values();
        ArrayList<User> users = new ArrayList<>(values);
        return ServiceResp.createBySuccess(users);
    }

    /**
     * @descript : 修改密码
     * @param name  用户名
     * @param newPwd   新密码
     * @param oldPwd  旧密码
     * @return
     */
    @RequestMapping("/modify_password")
    @ResponseBody
    public ServiceResp updatePassword(@NotBlank String name, String newPwd, String oldPwd){
         return userService.modifyPassword(name,newPwd,oldPwd);
    }

    /**
     * @description 下载用户手册
     * @param session
    * @param request
    * @param response
     * @return void
     * @author wangshuncheng
     * @date 2019/9/6 15:54
     */
    @RequestMapping("/minor_helpbook")
    public void getHelpBook(HttpSession session, HttpServletRequest request, HttpServletResponse response){
        userService.downloadHelpBook(session,request,response);
    }



}
