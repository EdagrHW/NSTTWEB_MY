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
 * @Description: ���������
 */
@Controller
public class TaskController {
	
    @Autowired
    private DetectionTaskService detectionTaskService;

    /**
     * @description ��ѯ����taskNumΪnull��ѯ���У�
     * @param taskNum �����
     * @author wangkai
     * @date 2019/9/17
     */
    @RequestMapping("task/query_task")
    @ResponseBody
    public ServiceResp<?> getTaskList(String taskNum) {
        return detectionTaskService.queryDetectionTask(taskNum);
    }


    /**
     * @description  �����ⱨ��
     * @param taskNum �����
     * @author zhouhuang
     * @date 2019/09/17
     */
    @RequestMapping("task/zip_report")
    @ResponseBody
    public ServiceResp<?> zipReport(String taskNum) {
        return detectionTaskService.createZipReport(taskNum);
    }

    /**
     * @description  ���ؼ�ⱨ��
     * @param path �����
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
     * @description �������
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
     * @description ɾ������
     * @param taskNum �����
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
     * @description �޸�����
     * @author wangkai
     * @date 2019/8/20
     */
    @RequestMapping("task/modify_task")
    @ResponseBody
    public ServiceResp<?> modifyDetectionTask(@RequestBody DetectionTask detectionTask) {
        String taskNum = detectionTask.getTaskNum();
        if ("".equals(taskNum) || taskNum == null) {
            return ServiceResp.createByErrorMessage("����Ų���Ϊ��");
        }
        if (!ConfigManager.getDetectionMap().containsKey(taskNum)) {
            return ServiceResp.createByErrorMessage("�����񲻴���");
        }
        /*
                        �ж������Ƿ���ִ��
        String msg = "";
        if (msg.contains("0")) {
            return ServiceResp.createByErrorMessage("��������ִ��,����ֹͣ��ȴ�����ִ��������޸�");
        }
        */
        
        // ����ԭ����
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
     * @description ��������
     * @param taskNum �����
     * @author wangkai
     * @date 2019/8/20
     */
    @RequestMapping("task/start_task")
    @ResponseBody
    public ServiceResp<?> startTask(String taskNum) {
        return detectionTaskService.startTask(taskNum);
    }

    /**
     * @description ֹͣ����
     * @param taskNum �����
     * @author wangkai
     * @date 2019/8/20
     */
    @RequestMapping("task/stop_task")
    @ResponseBody
    public ServiceResp<?> stopTask(String taskNum) {
        return detectionTaskService.stopTask(taskNum);
    }

    /**
     * @description ��������
     * @param taskNum �����
     * @author zhouhuang
     * @date 2019/9/10
     */
    @RequestMapping("task/restart_task")
    @ResponseBody
    public ServiceResp<?> restartTask(String taskNum) {
        return detectionTaskService.restartTask(taskNum);
    }

    /**
     * @description ��������
     * @author zhouhuang
     * @date 2019/9/10
     */
    @RequestMapping("task/clean_data")
    @ResponseBody
    public ServiceResp<?> cleanData(String taskNum) {
        return detectionTaskService.cleanData(taskNum);
    }


    /**
     * @description У�������Ƿ�����м�����
     * @param taskNum �����
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
