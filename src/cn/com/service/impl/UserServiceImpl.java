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
 * @description: ʵ������û���ҵ��
 * @date 2019/8/22 17:50
 */
@Service
public class UserServiceImpl implements UserService {

    private static TimedCache<String,String> timedCache = CacheUtil.newTimedCache(30*60*1000);

    private static ReadWriteLock rwlock = new ReentrantReadWriteLock();


    /**
     * @description
     * @param user  �û���Ϣ
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/11 10:17
     */
    @Override
    public ServiceResp addUser(User user) {
        String action = "����û�";
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
     * @param name  ���ÿ�����û���
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/11 10:18
     */
    @Override
    public ServiceResp modifyUser(String name) {
        String actio = "���ÿ���";
        User user = ConfigManager.getUserMap().get(name);
        if(user==null){
            return ServiceResp.createByErrorMessage("The user does not exist");
        }
        String newPassword = getPropertiesKey("defaultPassword");
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        // �������֮ǰ������
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
     * @param name  �û���
     * @return cn.com.infosec.common.ServiceResp
     * @author huangwei
     * @date 2020/09/03
     */
    @Override
    public ServiceResp getRouters(Integer level) {
    	List<RouterInfo> routers = new ArrayList<RouterInfo>();
    	if(level == RoleEnum.SYSADMIN.getLevel()) { // ϵͳ����Ա
    		RouterInfo parentRouter = new RouterInfo("������", "tools", 4) ;
    		RouterInfo children41 = new RouterInfo("�û�����", "usersmg", 41);
    		List<RouterInfo> childrens1 = new ArrayList<RouterInfo>();
    		childrens1.add(children41);
    		parentRouter.setChildren(childrens1);
    		
    		RouterInfo parentRouter2 = new RouterInfo("ϵͳ����", "system", 5) ;
    		RouterInfo children51 = new RouterInfo("����ϵͳʱ��", "systemtime", 51);
    		RouterInfo children52 = new RouterInfo("ϵͳ����", "systemup", 52);
    		List<RouterInfo> childrens2 = new ArrayList<RouterInfo>();
    		childrens2.add(children51);
    		childrens2.add(children52);
    		parentRouter2.setChildren(childrens2);
    	
    		routers.add(parentRouter);
    		routers.add(parentRouter2);
    	}else if(level == RoleEnum.TASKADMIN.getLevel()) {
    		
    		RouterInfo parentRouter = new RouterInfo("�������", "tasks", 1) ;
    		RouterInfo children11 = new RouterInfo("��������", "basic", 11);
    		RouterInfo children12 = new RouterInfo("��������", "cases", 12);
    		RouterInfo children13 = new RouterInfo("��������", "config", 13);
    		List<RouterInfo> childrens1 = new ArrayList<RouterInfo>();
    		childrens1.add(children11);
    		childrens1.add(children12);
    		childrens1.add(children13);
    		parentRouter.setChildren(childrens1);
    		
    		RouterInfo parentRouter2 = new RouterInfo("��־����", "journal", 2) ;
    		RouterInfo children21 = new RouterInfo("��־����", "log", 21);
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
     * @param name  ��Ҫɾ�����û���
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/11 10:18
     */
    @Override
    public ServiceResp deleteUser(String name) {
        String action = "ɾ���û�";
        // �������֮ǰ������
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
     * @param name   ��Ҫ�޸�������û���
    * @param newPwd    ������
    * @param oldPwd    ������
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/11 10:19
     */
    @Override
    public ServiceResp modifyPassword(String name,String newPwd,String oldPwd){
        String action = "�޸�����!";
        if(StrUtil.isBlank(name)){
             LogUtil.logFail(action, UserSession.get(Const.LOGINNAME), name, "name == null");
             return ServiceResp.createByErrorMessage("nameΪ��");
        }
        User user = ConfigManager.getUserMap().get(name);
        if(user == null){
            LogUtil.logFail(action, UserSession.get(Const.LOGINNAME), name, "���Ҳ������û�");
            return ServiceResp.createByErrorMessage("���Ҳ������û�");
        }
        if(newPwd.equals(oldPwd)){
            LogUtil.logFail(action, UserSession.get(Const.LOGINNAME), name, "�����޸�Ϊ��ǰ����");
            return ServiceResp.createByErrorMessage("�����޸�Ϊ��ǰ����");
        }
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String confirmPwd = md5.digestHex(name+oldPwd);
        if(!confirmPwd.equals(user.getPassword())){
            LogUtil.logFail(action, UserSession.get(Const.LOGINNAME), name, "������֤����");
            return ServiceResp.createByErrorMessage("������֤����");
        }else{
            // �������֮ǰ������
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
     * @param key  default.properties��key
     * @return    default.properties��Ӧkey��ֵ
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
        String action = "��ȡ�û��ֲ�";
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
            default: dealException(response,"δ֪��Ȩ��");
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
     * @param tips  ��ʾ��Ϣ
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
