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
 * @descript :  ��ɫ����
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
     * @description  ����û�
     * @param user   ����ӵ��û�����Ϣ
   * @param result
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/6 16:02
     */
    @RequestMapping("/add_user")
    @ResponseBody
    @LevelRole(RoleEnum.SYSADMIN)
    public ServiceResp addUser(@RequestBody @Validated User user, BindingResult result){
        String action = "����û�";
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
     * @param name  ��Ҫ���ÿ�����û���
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
     * @description  ɾ���û�
     * @param name   ��Ҫɾ�����û���
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
                return ServiceResp.createByErrorMessage("����ɾ����ǰ�����ڵ�¼���û�");
            }
        }
        return userService.deleteUser(name);

    }

    /**
     * @description  ��ѯ�û�·���б�
     * @return cn.com.infosec.common.ServiceResp
     * @author huangwei
     * @date 2020/09/03 16:04
     */
    @RequestMapping("/query_router")
    @ResponseBody
    public ServiceResp getRouters(Integer level){
    	if(level == null) {
    		return ServiceResp.createByErrorMessage("��ѯ·�ɲ���ʧ�ܣ�δ�����û�Ȩ�޵ȼ�");
    	}
        return userService.getRouters(level);
    }
    
    /**
     * @description  ��ѯ�����û�
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
     * @descript : �޸�����
     * @param name  �û���
     * @param newPwd   ������
     * @param oldPwd  ������
     * @return
     */
    @RequestMapping("/modify_password")
    @ResponseBody
    public ServiceResp updatePassword(@NotBlank String name, String newPwd, String oldPwd){
         return userService.modifyPassword(name,newPwd,oldPwd);
    }

    /**
     * @description �����û��ֲ�
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
