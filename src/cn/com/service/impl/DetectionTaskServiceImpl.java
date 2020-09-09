package cn.com.service.impl;

import cn.com.common.Const;
import cn.com.common.ServiceResp;
import cn.com.config.ConfigManager;
import cn.com.entity.DetectionTask;
import cn.com.entity.User;
import cn.com.entity.dto.UseCaseLibraryDto;
import cn.com.exception.BizException;
import cn.com.interceptor.UserSession;
import cn.com.service.DetectionTaskService;
import cn.com.util.Dom4jUtil;
import cn.com.util.LogUtil;
import cn.com.util.ShellUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import org.dom4j.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import static cn.com.util.FileUtil.readIniFile;


/**
 * @author zhaoxin
 * @description 实现类
 * @date 2019/8/12 18:17
 */
@Service
public class DetectionTaskServiceImpl implements DetectionTaskService {
    /**
     * 继续状态 没有报告记录且调用工具status返回值为-1
     */
    private static final int STATE_READY = -1;
    /**
     * 运行中
     */
    private static final int STATE_RUNNING = 0;

    /**
     * 未运行
     */
    private static final int STATE_NOT_RUNNING = 1;
    /**
     * 运行异常
     */
    private static final int STATE_ERROR = 2;


    /**
     * 任务状态 缓存默认5秒
     */
    private static final TimedCache<String, Integer> STATE_CACHE = new TimedCache<>(1000 * 5);

    /**
     * 依赖jar 缓存默认30秒
     */
    private static final TimedCache<String, List<String>> LIB_CACHE = new TimedCache<>(1000 * 60);
    /**
     * 依赖c库 缓存默认30秒
     */
    private static final TimedCache<String, List<String>> CLIB_CACHE = new TimedCache<>(1000 * 60);


    private static volatile List<String> GMT0018 = new ArrayList<>(Arrays.asList("algCheck.xml", "algPerf.xml", "random.xml"));

    @Override
    public ServiceResp<?> queryDetectionTask(String taskNum) {
    	ConcurrentMap<String, DetectionTask> detectionMap = ConfigManager.getDetectionMap();
    	if(taskNum == null) {
            return ServiceResp.createBySuccess(detectionMap);
    	}else {
    		 return ServiceResp.createBySuccess(detectionMap.get(taskNum));
    	}
        
    }


    @Override
    public ServiceResp<?> createZipReport(String taskNum){
    	 return ServiceResp.createBySuccess("下载报告成功");
    }

    @Override
    public void downloadTaskReport(String path, HttpServletRequest request, HttpServletResponse response){
        File downloadFile = new File(path);
        LogUtil.logInfo("download task report success", UserSession.get(Const.LOGINNAME), path, "success");
        String browser = request.getHeader("User-Agent");
        response.setContentType("application/octet-stream");
        try{
            ServletOutputStream outputStream = response.getOutputStream();
            FileInputStream inputStream = new FileInputStream(downloadFile);
            int b = 0;
            byte[] buffer = new byte[1024];
            while ((b = inputStream.read(buffer)) != -1) {
                // 4.写到输出流(out)中
                outputStream.write(buffer, 0, b);
            }
            inputStream.close();
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public ServiceResp<?> addDetectionTask(DetectionTask task) {
        return addDetectionTask(task, null);
    }


    private ServiceResp<?> addDetectionTask(DetectionTask task, String type) {
        // 判断任务号
        String taskNum = task.getTaskNum();
        if ("".equals(taskNum) || taskNum == null) {
            LogUtil.logFail("add detection task fail", UserSession.get(Const.LOGINNAME), task, "任务号不能为空");
            return ServiceResp.createByErrorMessage("任务号不能为空");
        }
        if (ConfigManager.getDetectionMap().get(taskNum) != null && !"modify".equals(type)) {
            LogUtil.logFail("add detection task fail", UserSession.get(Const.LOGINNAME), task, "该任务已创建,无需再次创建");
            return ServiceResp.createByErrorMessage("该任务已创建,无需再次创建");
        }

        File file = new File(Const.DETECTION_DATA_PATH + taskNum);
        if (file.exists() && file.isDirectory()) {
            LogUtil.logFail("add detection task fail", UserSession.get(Const.LOGINNAME), task, "该任务已被创建");
            return ServiceResp.createByErrorMessage("该任务已被创建");
        }
        String caselib = task.getCaseLib();
        UseCaseLibraryDto useCaseLibraryDto = ConfigManager.getCaseMap().get(caselib);
        String caseNameUUID = useCaseLibraryDto.getUseCaseLibrary().getCaseNameUUID();
        String libPath = Const.CASE_LIB_PATH + caseNameUUID;
        File libFile = new File(libPath);
        if (!libFile.exists() || !libFile.isDirectory()) {
            LogUtil.logFail("add detection task fail", UserSession.get(Const.LOGINNAME), task, "用例库不存在");
            return ServiceResp.createByErrorMessage("用例库不存在");
        } else if (libFile.list() == null || Objects.requireNonNull(libFile.list()).length < 1) {
            LogUtil.logFail("add detection task fail", UserSession.get(Const.LOGINNAME), task, "该用例库已无法使用,请重新选择用例库");
            return ServiceResp.createByErrorMessage("该用例库已无法使用,请重新选择用例库");
        }
        String rootPath = Const.DETECTION_DATA_PATH  + taskNum;
        try {
            cn.com.util.FileUtil.createNewFileDir(Const.DETECTION_DATA_PATH, taskNum);
            cn.com.util.FileUtil.createNewFileDir(rootPath, "basicInfo", "recordData", "testCase");
            String basicInfoPath = rootPath + File.separator + "basicInfo";
            File taskBasicInfoFile = new File(basicInfoPath + File.separator + "taskBasicInfo.xml");
            if (!taskBasicInfoFile.exists()) {
                cn.com.util.FileUtil.createNewFile(basicInfoPath, "taskBasicInfo.xml");
            }
            cn.hutool.core.io.FileUtil.writeString(Dom4jUtil.toStr(task.toBasicInfoXml()), taskBasicInfoFile, "UTF-8");
            String testCasePath = rootPath + File.separator + "testCase";
            
            cn.com.util.FileUtil.copyFolder(libPath, testCasePath);
            
            String detectionPath = Const.WEB_CONFIG_PATH + "detectionTask.xml";
            File detectionFile = new File(detectionPath);
            if (!detectionFile.exists()) {
                cn.com.util.FileUtil.createNewFile(Const.WEB_CONFIG_PATH, "detectionTask.xml");
            }
            if ("modify".equals(type)) {
                ConfigManager.getDetectionMap().remove(taskNum);
            }
            task.setLastModifyDate(new Date());
            ConfigManager.getDetectionMap().put(taskNum, task);
            ConfigManager.genDetectionTaskXml();
            LogUtil.logInfo("add detection task success", UserSession.get(Const.LOGINNAME), task, "success");
        } catch (Exception e) {
            cn.com.util.FileUtil.deleteDirectory(rootPath);
            LogUtil.logException("add detection task fail", UserSession.get(Const.LOGINNAME), null, e);
            throw new BizException(e);
        }
        return ServiceResp.createBySuccess();
    }

    /**
     * 删除任务
     * 1.从配置文件移除
     * 2.调用任务stop  调用检测工具clean命令
     * 3.删除相关库的目录
     *
     * @param taskNum 任务号
     */
    @Override
    public ServiceResp<?> deleteDetectionTask(String taskNum) {
        // 查询任务状态
        String rootPath = Const.DETECTION_DATA_PATH + taskNum;
        File taskFile = new File(rootPath);
        if (!taskFile.exists() && !taskFile.isDirectory()) {
            ConfigManager.getDetectionMap().remove(taskNum);
            ConfigManager.genDetectionTaskXml();
            return ServiceResp.createBySuccess("删除成功");
        }
        
        ConcurrentMap<String, DetectionTask> detectionMap = ConfigManager.getDetectionMap();
        
        if (detectionMap.get(taskNum).getState() != STATE_RUNNING) {
            try {
            	detectionMap.remove(taskNum);
                cn.com.util.FileUtil.deleteDirectory(rootPath);
                ConfigManager.genDetectionTaskXml();
                //需要删除任务相关文件夹
                LogUtil.logInfo("del detection task success", UserSession.get(Const.LOGINNAME), taskNum, null);
                return ServiceResp.createBySuccess("任务删除成功");
            } catch (Exception e) {
                LogUtil.logException("del detection task fail fail", UserSession.get(Const.LOGINNAME), taskNum, e);
                return ServiceResp.createByErrorMessage("任务删除失败");
            }
        } else {
            return ServiceResp.createByErrorMessage("任务正在运行中无法删除");
        }
    }

    @Override
    public ServiceResp<?> modifyDetectionTask(DetectionTask task) {
        // 判断任务号
        String taskNum = task.getTaskNum();
        String path = Const.DETECTION_DATA_PATH + taskNum;
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            return addDetectionTask(task, "midify");
        }
        String detectionPath;
        ShellUtil.backOrRoll(Const.WEB_CONFIG_PATH, "detectionTask.xml", "back");
        detectionPath = Const.WEB_CONFIG_PATH + "detectionTask.xml";
        
        String backPath = Const.DETECTION_DATA_PATH + "tmp";
        cn.com.util.FileUtil.copyFolder(path, backPath);
        try {
            //替换用例库
            String caselib = task.getCaseLib();
            cn.com.util.FileUtil.deleteDirectory(path + File.separator + "testCase");
            String caseNameUUID = ConfigManager.getCaseMap().get(caselib).getUseCaseLibrary().getCaseNameUUID();
            String libPath = Const.CASE_LIB_PATH + caseNameUUID;
            String testCasePath = path + File.separator + "testCase";
            cn.com.util.FileUtil.copyFolder(libPath, testCasePath);
            //修改detectionTask.xml
            File detectionFile = new File(detectionPath);
            if (!detectionFile.exists()) {
                cn.com.util.FileUtil.createNewFile(Const.WEB_CONFIG_PATH, "detectionTask.xml");
            }
            ConfigManager.getDetectionMap().remove(taskNum);
            ConfigManager.getDetectionMap().put(taskNum, task);
            ConfigManager.genDetectionTaskXml();
        } catch (Exception e) {
            LogUtil.logException("modify detection task fail fail", UserSession.get(Const.LOGINNAME), taskNum, e);
            throw new BizException(e);
        }
        LogUtil.logInfo("modify detection task success", UserSession.get(Const.LOGINNAME), null, taskNum);
        return ServiceResp.createBySuccess();
    }

    /**
     * 启动任务
     * 1.判断 到detectionData任务编号目录检测用例
     * 2.判断 启动之前要上传用例 配置init.xml
     * 3.判断 是否正在执行
     *
     * @param taskNum 任务号
     */
    @Override
    public ServiceResp<?> startTask(String taskNum) {
        ServiceResp<?> serviceResp = checkTaskIsOk(taskNum);
        if (!serviceResp.getSuccess()) {
            LogUtil.logInfo("startTask detection task fail:", UserSession.get(Const.LOGINNAME), taskNum, serviceResp);
            return ServiceResp.createBySuccess(serviceResp);
        }
        String rootPath = Const.DETECTION_DATA_PATH + taskNum;
        String caseLib = ConfigManager.getDetectionMap().get(taskNum).getCaseLib();

        if (ConfigManager.getCaseMap().get(caseLib) == null) {
            return ServiceResp.createByErrorMessage("没有用例库无法开启");
        } else if (ConfigManager.getCaseMap().get(caseLib).getUseCaseLibrary() == null) {
            return ServiceResp.createByErrorMessage("没有用例库无法开启");
        }
        List<String> casesName = ConfigManager.getCaseMap().get(caseLib).getUseCaseLibrary().getCasesName();
        if (casesName.isEmpty()) {
            LogUtil.logInfo("startTask detection task fail", UserSession.get(Const.LOGINNAME), taskNum, "缺少必要的依赖库无法开启,请先修改后再次尝试开启");
            return ServiceResp.createByErrorMessage("该用例库无用例可用,请先修改后再次尝试开启");
        }

        File taskFile = new File(rootPath);
        if (!taskFile.exists() || !taskFile.isDirectory()) {
            LogUtil.logInfo("startTask detection task fail", UserSession.get(Const.LOGINNAME), taskNum, "该任务已无法执行,请先修改后再次执行");
            return ServiceResp.createByErrorMessage("该任务已无法执行,请先修改后再次执行");
        }
        
        ConfigManager.getDetectionMap().get(taskNum).setLastModifyDate(new Date());
        ConfigManager.getDetectionMap().get(taskNum).setLastDate(new Date());
        ConfigManager.genDetectionTaskXml();
        LogUtil.logInfo("startTask detection task success", UserSession.get(Const.LOGINNAME), taskNum, null);
        return ServiceResp.createBySuccess("任务启动成功");
    }

    @Override
    public ServiceResp<?> stopTask(String taskNum) {
        ServiceResp<?> serviceResp = checkTaskIsOk(taskNum);
        if (!serviceResp.getSuccess()) {
            LogUtil.logInfo("stopTask detection task fail", UserSession.get(Const.LOGINNAME), taskNum, serviceResp);
            return serviceResp;
        }
        return ServiceResp.createBySuccess("任务停止成功");
    }



    @Override
    public ServiceResp<?> restartTask(String taskNum) {
        ServiceResp<?> serviceResp = checkTaskIsOk(taskNum);
        if (!serviceResp.getSuccess()) {
            LogUtil.logInfo("restartTask detection task fail:", UserSession.get(Const.LOGINNAME), taskNum, serviceResp);
            return ServiceResp.createBySuccess(serviceResp);
        }
        String rootPath = Const.DETECTION_DATA_PATH + taskNum;
        String caseLib = ConfigManager.getDetectionMap().get(taskNum).getCaseLib();

        if (ConfigManager.getCaseMap().get(caseLib) == null) {
            return ServiceResp.createByErrorMessage("没有用例库无法开启");
        } else if (ConfigManager.getCaseMap().get(caseLib).getUseCaseLibrary() == null) {
            return ServiceResp.createByErrorMessage("没有用例库无法开启");
        }
        List<String> casesName = ConfigManager.getCaseMap().get(caseLib).getUseCaseLibrary().getCasesName();
        if (casesName.isEmpty()) {
            LogUtil.logInfo("restartTask detection task fail", UserSession.get(Const.LOGINNAME), taskNum, "缺少必要的依赖库无法开启,请先修改后再次尝试开启");
            return ServiceResp.createByErrorMessage("该用例库无用例可用,请先修改后再次尝试开启");
        }

        File taskFile = new File(rootPath);
        if (!taskFile.exists() || !taskFile.isDirectory()) {
            LogUtil.logInfo("restartTask detection task fail", UserSession.get(Const.LOGINNAME), taskNum, "该任务已无法执行,请先修改后再次执行");
            return ServiceResp.createByErrorMessage("该任务已无法执行,请先修改后再次执行");
        }
        ConfigManager.getDetectionMap().get(taskNum).setLastModifyDate(new Date());
        ConfigManager.getDetectionMap().get(taskNum).setLastDate(new Date());
        ConfigManager.genDetectionTaskXml();
        LogUtil.logInfo("restartTask detection task success", UserSession.get(Const.LOGINNAME), taskNum, null);
        return ServiceResp.createBySuccess("任务重启成功");
    }


    @Override
    public ServiceResp<?> cleanData(String taskNum) {
    	
    	String logPath = Const.SSLVPN_LOG_PATH + taskNum;
    	cn.com.util.FileUtil.deleteDirectory(logPath);
    	return ServiceResp.createBySuccess("任务清理成功");
    }

    private ServiceResp<?> checkTaskIsOk(String taskNum) {
        Map<String, Object> map;
        String rootPath = Const.DETECTION_DATA_PATH + taskNum;
        File file = new File(rootPath);
        if (!file.exists() || !file.isDirectory()) {
            LogUtil.logInfo("checkTaskIsOk detection task fail", UserSession.get(Const.LOGINNAME), taskNum, "任务不存在");
            return ServiceResp.createByErrorMessage("任务不存在");
        }
        return ServiceResp.createBySuccess("任务OK");
    }

    @Override
    public ServiceResp<?> checkTask(String taskNum) {
        String path = Const.INTERMEDIATE_DATA_PATH + taskNum;
        File file = new File(path);
        boolean isDir = file.exists() && file.isDirectory();
        try {
            if (isDir && file.list().length > 0) {
                return ServiceResp.createBySuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResp.createByError();
        }
        return ServiceResp.createByError();

    }

    
 
}
