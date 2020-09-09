package cn.com.controller;

import cn.com.aop.LevelRole;
import cn.com.common.Const;
import cn.com.common.RoleEnum;
import cn.com.common.ServiceResp;
import cn.com.config.ConfigManager;
import cn.com.entity.DetectionTask;
import cn.com.interceptor.UserSession;
import cn.com.service.DetectionTaskService;
import cn.com.util.FileUtil;
import cn.com.util.LogUtil;
import cn.com.util.UploadUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;


/**
 * @author WangKai
 * @ClassName: TaskController
 * @date 2019-08-20 13:41
 * @Description: 任务管理类
 */
@Controller
public class TaskController {
	
    @Autowired
    private DetectionTaskService detectionTaskService;

    /**
     * @description 查询任务（taskNum为null查询所有）
     * @param taskNum 任务号
     * @author wangkai
     * @date 2019/9/17
     */
    @RequestMapping("task/query_task")
    @ResponseBody
    public ServiceResp<?> getTaskList(String taskNum) {
        return detectionTaskService.queryDetectionTask(taskNum);
    }


    /**
     * @description  打包检测报告
     * @param taskNum 任务号
     * @author zhouhuang
     * @date 2019/09/17
     */
    @RequestMapping("task/zip_report")
    @ResponseBody
    public ServiceResp<?> zipReport(String taskNum) {
        return detectionTaskService.createZipReport(taskNum);
    }

    /**
     * @description  下载检测报告
     * @param path 任务号
     * @author zhouhuang
     * @date 2019/09/17
     */
    @RequestMapping("task/download_report")
    public void downloadReport(String path, HttpServletRequest request, HttpServletResponse response) {
        try{
            path = new String(path.getBytes("ISO8859-1"), "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        detectionTaskService.downloadTaskReport(path, request, response);
    }

    /**
     * @description 添加任务
     * @author wangkai
     * @date 2019/8/20 10:57
     */
    @RequestMapping("task/add_task")
    @ResponseBody
    public ServiceResp<?> addTask(@RequestBody DetectionTask detectionTask) {
        ServiceResp<?> success = detectionTaskService.addDetectionTask(detectionTask);
        if (success.getSuccess()) {
            ConfigManager.getDetectionMap().put(detectionTask.getTaskNum(), detectionTask);
        }else {
            return success;
        }
        return ServiceResp.createBySuccess();
    }


    /**
     * @description 删除任务
     * @param taskNum 任务号
     * @author wangkai
     * @date 2019/8/20
     */
    @RequestMapping("task/delete_task")
    @ResponseBody
    @LevelRole({RoleEnum.SECURITYADMIN})
    public ServiceResp<?> deleteDetectionTask(String taskNum) {
        return detectionTaskService.deleteDetectionTask(taskNum);
    }

    /**
     * @description 修改任务
     * @author wangkai
     * @date 2019/8/20
     */
    @RequestMapping("task/modify_task")
    @ResponseBody
    public ServiceResp<?> modifyDetectionTask(@RequestBody DetectionTask detectionTask) {
        String taskNum = detectionTask.getTaskNum();
        if ("".equals(taskNum) || taskNum == null) {
            return ServiceResp.createByErrorMessage("任务号不能为空");
        }
        if (!ConfigManager.getDetectionMap().containsKey(taskNum)) {
            return ServiceResp.createByErrorMessage("该任务不存在");
        }
        /*
                        判断任务是否在执行
        String msg = "";
        if (msg.contains("0")) {
            return ServiceResp.createByErrorMessage("任务正在执行,请先停止或等待任务执行完成再修改");
        }
        */
        
        // 备份原任务
        String dir = Const.DETECTION_DATA_PATH + File.separator + taskNum;
        String backDir = Const.DETECTION_DATA_PATH + File.separator + "tmp";
        FileUtil.copyFolder(dir, backDir);
        
        ServiceResp<?>success;
        try {
        	success= detectionTaskService.modifyDetectionTask(detectionTask);
            if (success.getSuccess()) {
                ConfigManager.getDetectionMap().put(detectionTask.getTaskNum(), detectionTask);
            } else {
            	 FileUtil.copyFolder(backDir, dir);
            }
        } finally {
            FileUtil.deleteDirectory(backDir);
        }
        return success;
            
        
    }

    /**
     * @description 启动任务
     * @param taskNum 任务号
     * @author wangkai
     * @date 2019/8/20
     */
    @RequestMapping("task/start_task")
    @ResponseBody
    public ServiceResp<?> startTask(String taskNum) {
        return detectionTaskService.startTask(taskNum);
    }

    /**
     * @description 停止任务
     * @param taskNum 任务号
     * @author wangkai
     * @date 2019/8/20
     */
    @RequestMapping("task/stop_task")
    @ResponseBody
    public ServiceResp<?> stopTask(String taskNum) {
        return detectionTaskService.stopTask(taskNum);
    }

    /**
     * @description 重启任务
     * @param taskNum 任务号
     * @author zhouhuang
     * @date 2019/9/10
     */
    @RequestMapping("task/restart_task")
    @ResponseBody
    public ServiceResp<?> restartTask(String taskNum) {
        return detectionTaskService.restartTask(taskNum);
    }

    /**
     * @description 清理数据
     * @author zhouhuang
     * @date 2019/9/10
     */
    @RequestMapping("task/clean_data")
    @ResponseBody
    public ServiceResp<?> cleanData(String taskNum) {
        return detectionTaskService.cleanData(taskNum);
    }


    /**
     * @description 校验任务是否存在中间数据
     * @param taskNum 任务号
     * @author wangkai
     * @date 2019/8/22
     */
    @RequestMapping("task/modify_check_task")
    @ResponseBody
    public ServiceResp<?> checkTask(String taskNum) {
        try {
            return detectionTaskService.checkTask(taskNum);
        } catch (Exception e) {
            return ServiceResp.createByError();
        }
    }
    
}
