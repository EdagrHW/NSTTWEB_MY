package cn.com.service.impl;

import cn.com.common.Const;
import cn.com.common.RoleEnum;
import cn.com.common.ServiceResp;
import cn.com.config.ConfigManager;
import cn.com.config.MyHttpSessionListener;
import cn.com.entity.RouterInfo;
import cn.com.entity.User;
import cn.com.exception.BizException;
import cn.com.interceptor.UserSession;
import cn.com.service.UserService;
import cn.com.util.FileUtil;
import cn.com.util.LogUtil;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.setting.dialect.Props;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author wsc
 * @description: 实现添加用户的业务
 * @date 2019/8/22 17:50
 */
@Service
public class UserServiceImpl implements UserService {

    private static TimedCache<String,String> timedCache = CacheUtil.newTimedCache(30*60*1000);

    private static ReadWriteLock rwlock = new ReentrantReadWriteLock();


    /**
     * @description
     * @param user  用户信息
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/11 10:17
     */
    @Override
    public ServiceResp addUser(User user) {
        String action = "添加用户";
        User newUser = ConfigManager.getUserMap().get(user.getName());
        if(newUser!=null){
            LogUtil.logFail(action, UserSession.get(Const.LOGINNAME),null,"The username already exists");
            return  ServiceResp.createByErrorMessage("The username already exists");
        }else{
            String uuid  = UUID.randomUUID().toString();
            user.setId(uuid.replaceAll("-",""));
            Digester md5 = new Digester(DigestAlgorithm.MD5);
            user.setPassword(md5.digestHex(user.getName()+user.getPassword()));
            ConfigManager.userMap.put(user.getName(),user);
            try{
                ConfigManager.writerUserXML();
                LogUtil.logInfo(action, UserSession.get(Const.LOGINNAME),null,"success");
                return ServiceResp.createBySuccess();
            }catch (BizException e){
                if(ConfigManager.userMap.get(user.getName())!=null){
                    ConfigManager.userMap.remove(user.getName());
                }
                LogUtil.logException(action, UserSession.get(Const.LOGINNAME),null,e);
                return ServiceResp.createByErrorMessage(e.getMessage());
            }
        }
    }


    /**
     * @description
     * @param name  重置口令的用户名
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/11 10:18
     */
    @Override
    public ServiceResp modifyUser(String name) {
        String actio = "重置口令";
        User user = ConfigManager.getUserMap().get(name);
        if(user==null){
            return ServiceResp.createByErrorMessage("The user does not exist");
        }
        String newPassword = getPropertiesKey("defaultPassword");
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        // 缓存放入之前的数据
        user.setPassword(md5.digestHex(name+newPassword));
        try{
            ConfigManager.writerUserXML();
            LogUtil.logSuccess(actio,UserSession.get(Const.LOGINNAME),name);
            MyHttpSessionListener.destorySession(name);
            return ServiceResp.createBySuccess();
        }catch (BizException e){
            ConfigManager.readUserMXL();
            LogUtil.logException(actio,UserSession.get(Const.LOGINNAME),name,e);
            return ServiceResp.createByErrorMessage(e.getMessage());
        }
    }
    
    /**
     * @description
     * @param name  用户名
     * @return cn.com.infosec.common.ServiceResp
     * @author huangwei
     * @date 2020/09/03
     */
    @Override
    public ServiceResp getRouters(Integer level) {
    	List<RouterInfo> routers = new ArrayList<RouterInfo>();
    	if(level == RoleEnum.SYSADMIN.getLevel()) { // 系统管理员
    		RouterInfo parentRouter = new RouterInfo("管理工具", "tools", 4) ;
    		RouterInfo children41 = new RouterInfo("用户管理", "usersmg", 41);
    		List<RouterInfo> childrens1 = new ArrayList<RouterInfo>();
    		childrens1.add(children41);
    		parentRouter.setChildren(childrens1);
    		
    		RouterInfo parentRouter2 = new RouterInfo("系统管理", "system", 5) ;
    		RouterInfo children51 = new RouterInfo("设置系统时间", "systemtime", 51);
    		RouterInfo children52 = new RouterInfo("系统升级", "systemup", 52);
    		List<RouterInfo> childrens2 = new ArrayList<RouterInfo>();
    		childrens2.add(children51);
    		childrens2.add(children52);
    		parentRouter2.setChildren(childrens2);
    	
    		routers.add(parentRouter);
    		routers.add(parentRouter2);
    	}else if(level == RoleEnum.TASKADMIN.getLevel()) {
    		
    		RouterInfo parentRouter = new RouterInfo("任务管理", "tasks", 1) ;
    		RouterInfo children11 = new RouterInfo("基本配置", "basic", 11);
    		RouterInfo children12 = new RouterInfo("用例管理", "cases", 12);
    		RouterInfo children13 = new RouterInfo("任务配置", "config", 13);
    		List<RouterInfo> childrens1 = new ArrayList<RouterInfo>();
    		childrens1.add(children11);
    		childrens1.add(children12);
    		childrens1.add(children13);
    		parentRouter.setChildren(childrens1);
    		
    		RouterInfo parentRouter2 = new RouterInfo("日志管理", "journal", 2) ;
    		RouterInfo children21 = new RouterInfo("日志管理", "log", 21);
    		List<RouterInfo> childrens2 = new ArrayList<RouterInfo>();
    		childrens2.add(children21);
    		parentRouter2.setChildren(childrens2);
    	
    		routers.add(parentRouter);
    		routers.add(parentRouter2);
    		
    	}
    	return ServiceResp.createBySuccess(routers);
    }

    /**
     * @description
     * @param name  需要删除的用户名
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/11 10:18
     */
    @Override
    public ServiceResp deleteUser(String name) {
        String action = "删除用户";
        // 缓存放入之前的数据
        ConfigManager.userMap.remove(name);
        try{
            ConfigManager.writerUserXML();
            MyHttpSessionListener.destorySession(name);
            LogUtil.logInfo(action,UserSession.get(Const.LOGINNAME),name,"success");
            return ServiceResp.createBySuccess();
        }catch (BizException e){
            ConfigManager.readUserMXL();
            LogUtil.logException(action,UserSession.get(Const.LOGINNAME),name,e);
            return ServiceResp.createByErrorMessage(e.getMessage());
        }
    }

    /**
     * @description
     * @param name   需要修改密码的用户名
    * @param newPwd    新密码
    * @param oldPwd    旧密码
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/11 10:19
     */
    @Override
    public ServiceResp modifyPassword(String name,String newPwd,String oldPwd){
        String action = "修改密码!";
        if(StrUtil.isBlank(name)){
             LogUtil.logFail(action, UserSession.get(Const.LOGINNAME), name, "name == null");
             return ServiceResp.createByErrorMessage("name为空");
        }
        User user = ConfigManager.getUserMap().get(name);
        if(user == null){
            LogUtil.logFail(action, UserSession.get(Const.LOGINNAME), name, "查找不到该用户");
            return ServiceResp.createByErrorMessage("查找不到该用户");
        }
        if(newPwd.equals(oldPwd)){
            LogUtil.logFail(action, UserSession.get(Const.LOGINNAME), name, "不能修改为当前密码");
            return ServiceResp.createByErrorMessage("不能修改为当前密码");
        }
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String confirmPwd = md5.digestHex(name+oldPwd);
        if(!confirmPwd.equals(user.getPassword())){
            LogUtil.logFail(action, UserSession.get(Const.LOGINNAME), name, "密码验证错误");
            return ServiceResp.createByErrorMessage("密码验证错误");
        }else{
            // 缓存放入之前的数据
            user.setPassword(md5.digestHex(name+newPwd));
            try{
                ConfigManager.writerUserXML();
                MyHttpSessionListener.destorySession(name);
                LogUtil.logSuccess(action, UserSession.get(Const.LOGINNAME), name);
                return ServiceResp.createBySuccess();
            }catch (BizException e){
                ConfigManager.readUserMXL();
                LogUtil.logException(action, UserSession.get(Const.LOGINNAME), name, e);
                return ServiceResp.createByErrorMessage(e.getMessage());
            }

        }


    }


    /**
     * @param key  default.properties的key
     * @return    default.properties对应key的值
     */
    public static String getPropertiesKey(String key){
        String value = timedCache.get(key,false);
        if(value==null){
            value = getkeyValue(key);
            timedCache.put(key,value);
            return value;
        }else{
            return value;
        }
    }

    private static String getkeyValue(String key){
        rwlock.readLock().lock();
        try {
            Props props = new Props(Const.DEFAULT_CONFIG, "utf-8");
            String value = props.getStr(key);
            return value;
        }finally {
            rwlock.readLock().unlock();
        }
    }


    /**
     * @description
     * @param session
    * @param request
    * @param response
     * @return void
     * @author wangshuncheng
     * @date 2019/9/11 10:21
     */
    @Override
    public void downloadHelpBook(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String action = "获取用户手册";
        String requestUrl = request.getHeader("REFERER");
        Object user = session==null?null:session.getAttribute(Const.LOGINNAME);
        int level = ((User)user).getLevel();
        File file = new File(Const.HELPBOOK + File.separator + Const.HELPBOOK_PDF);
        /*switch (level) {
            case 1: file = new File(Const.HELPBOOK,Const.HELPBOOK_VIEW);
                break;
            case 2:file = new File(Const.HELPBOOK,Const.HELPBOOK_TASK);
                break;
            case 3:file = new File(Const.HELPBOOK,Const.HELPBOOK_SYS);
                break;
            default: dealException(response,"未知的权限");
        }*/
        try {
            byte[] data = FileUtil.fileTransToArrayByte(file);
            response.addHeader("Content-Disposition", "attachment; filename=helpbook.pdf");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Length", data.length + "");
            OutputStream out = response.getOutputStream();
            out.write(data);
            out.flush();
            LogUtil.logInfo(action, UserSession.get(Const.LOGINNAME),file.getName(),null);
        } catch (IOException e) {
            LogUtil.logException(action,UserSession.get(Const.LOGINNAME),file.getName(),e);
            LogUtil.logInfo(action, UserSession.get(Const.LOGINNAME),file.getName(),"the download file fail");
        }
    }

    /**
     *
     * @param response
     * @param tips  提示信息
     */
    private void dealException(HttpServletResponse response, String tips){
        try {
            PrintWriter printWriter = response.getWriter();
            String alert = "<script>alert('download help book throws Exception:"+tips+", please check and try again !!!');location.href='"+Const.HOME_PAGE+"'</script>";
            printWriter.write(alert);
            printWriter.flush();
            printWriter.close();
        }catch (IOException ignored){

        }
    }
}
