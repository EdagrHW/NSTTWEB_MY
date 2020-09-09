package cn.com.controller;

import cn.com.aop.LevelRole;
import cn.com.common.Const;
import cn.com.common.RoleEnum;
import cn.com.common.ServiceResp;
import cn.com.interceptor.UserSession;
import cn.com.service.SysinfoService;
import cn.com.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @descript :  查询系统的一些基本信息
 *
 * @author : wangshuncheng
 * @date : Create in 16:07 2019/8/15
 */
@RestController
@RequestMapping("/sysinfo")
public class SysInfoController {

    @Autowired
    private SysinfoService sysinfoService;

    /**
     * @description 修改系统时间
     * @param date  修改修改为的时间
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/6 15:57
     */
    @RequestMapping("/modify_sysdate")
    @LevelRole(RoleEnum.SYSADMIN)
    @ResponseBody
    public ServiceResp setDate(@NotBlank String date){
        String action = "修改时间";
        if(date != null && date.trim().length() !=19){
            return ServiceResp.createByErrorMessage("不符合规格要求的时间参数");
        }
        try{
        	String cmd = "date -s " + "'" + date + "'";
        	String[] cmdA = { "/bin/sh", "-c", cmd };
        	System.out.println("执行命令: " + cmd);
			Runtime.getRuntime().exec(cmdA); // 修改日期
		}catch(Exception e){
			e.printStackTrace();
			return ServiceResp.createByErrorMessage("系统时间修改失败");
		}

        return ServiceResp.createBySuccess();

    }

    /**
     * @description  返回系统的时间
     * @return cn.com.infosec.common.ServiceResp
     * @author wangshuncheng
     * @date 2019/9/6 15:57
     */
    @RequestMapping("/query_sysdate")
    @LevelRole(RoleEnum.SYSADMIN)
    @ResponseBody
    public ServiceResp getDate(){
    	
        String action = "获取时间";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(new Date());
        System.out.println("time = " + time);
        
        return ServiceResp.createBySuccess(time);
    }

    /**
     * @description  上传升级文件并执行升级
     * @param file  文件流
     * @return cn.com.infosec.common.ServiceResp
     * @author  zhouhuang
     * @date 2019/11/29 15:12
     */
    @RequestMapping("/sysUpdate")
    @ResponseBody
    @LevelRole({RoleEnum.SYSADMIN})
    public ServiceResp uploadLicence(@RequestParam("file") MultipartFile file){

        String action = "系统升级";
        try {
            byte[] data = file.getBytes();
            return sysinfoService.sysUpdate(data);
        }catch (IOException e){
            LogUtil.logException(action,UserSession.get(Const.LOGINNAME),"byte[]:file--",e);
            return ServiceResp.createByErrorMessage(e.getMessage());
        }

    }

    /**
     * @description  获取升级日志
     * @return cn.com.infosec.common.ServiceResp
     * @author  zhouhuang
     * @date 2019/11/29 15:12
     */
    @RequestMapping("/getUpgradeLog")
    @ResponseBody
    @LevelRole({RoleEnum.SYSADMIN})
    public ServiceResp uploadLicence(){
        return sysinfoService.getUpgradeLog();
    }

    /**
     * @description  查看升级记录
     * @return cn.com.infosec.common.ServiceResp
     * @author  zhouhuang
     * @date 2019/11/29 15:12
     */
    @RequestMapping("/viewUpdateLog")
    @ResponseBody
    @LevelRole({RoleEnum.SYSADMIN})
    public ServiceResp viewUpdateLog(){
        return sysinfoService.viewUpdateLog();
    }

    /**
     * @description  重启系统
     * @return cn.com.infosec.common.ServiceResp
     * @author  zhouhuang
     * @date 2019/11/29 15:12
     */
    @RequestMapping("/reboot")
    @ResponseBody
    @LevelRole({RoleEnum.SYSADMIN})
    public ServiceResp reboot(){
        return sysinfoService.reboot();
    }








}
