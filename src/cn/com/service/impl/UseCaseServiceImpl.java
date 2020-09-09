package cn.com.service.impl;

import cn.com.common.Const;
import cn.com.common.ServiceResp;
import cn.com.config.ConfigManager;
import cn.com.entity.CaseInfo;
import cn.com.entity.DetectionTask;
import cn.com.entity.UseCaseLibrary;
import cn.com.entity.dto.UseCaseLibraryDto;
import cn.com.exception.BizException;
import cn.com.interceptor.UserSession;
import cn.com.service.UseCaseService;
import cn.com.util.Dom4jUtil;
import cn.com.util.FileUtil;
import cn.com.util.LogUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


/**
 * @author WangKai
 * @ClassName: UseCaseManager
 * @date 2019-08-16 15:29
 * @Description:
 */
@Service
public class UseCaseServiceImpl implements UseCaseService {


    @Override
    public ServiceResp<?> addCase(UseCaseLibraryDto useCaseLibraryDto) {
        useCaseLibraryDto.getUseCaseLibrary().setTime(new Date());
        return addUseCase(useCaseLibraryDto, null);
    }

    private ServiceResp<?> addUseCase(UseCaseLibraryDto useCaseLibraryDto, String type) {
        UseCaseLibrary useCaseLibrary = useCaseLibraryDto.getUseCaseLibrary();
        String caseName = useCaseLibrary.getCaseName();
        if (ConfigManager.getCaseMap().get(caseName) != null && !"modify".equals(type)) {
            return ServiceResp.createByErrorMessage("该用例库已存在,不允许添加");
        }if(!"modify".equals(type)) {
        	 // 生成caseNameUUID
            useCaseLibrary.getUUID();
        }
        String caseNameUUID = useCaseLibrary.getCaseNameUUID();
        String dir = Const.CASE_LIB_PATH + caseNameUUID.trim();
        
        List<CaseInfo> cases = useCaseLibrary.getCases();
        StringBuilder files = new StringBuilder();
        List<String> caseList = new ArrayList<>();
        System.out.println("cases = " + cases);
        for (CaseInfo caseInfo : cases) {
        	System.out.println(caseInfo);
            caseList.add(caseInfo.getCaseName());
        }
        if(caseList.size() > 0){
            String deviceType = useCaseLibrary.getDeviceType();
            String mode = useCaseLibrary.getMode();
            String caseLibPath = Const.TEST_CASE_PATH + deviceType + File.separator + mode + File.separator;
            if("CSMODE".equals(mode)){
                for(String caseName1 : caseList){
                    FileUtil.copyFolder(caseLibPath + caseName1, dir);
                }
            }
        }
        File file = new File(Const.WEB_CONFIG_PATH + "cases.xml");
        if (!file.exists()) {
            FileUtil.createNewFile(Const.WEB_CONFIG_PATH, "cases.xml");
        }
        useCaseLibrary.setCasesName(caseList);
        if ("modify".equals(type)) {
            ConfigManager.getCaseMap().remove(caseName);
        }
        try {
        	ConfigManager.getCaseMap().put(caseName, useCaseLibraryDto);
            ConfigManager.genCaseXml();
		} catch (Exception e) {
			if("modify".equals(type)) {
				return ServiceResp.createByErrorMessage("修改用例库失败!");
			}else {
				return ServiceResp.createByErrorMessage("保存用例库失败!");
			}
		}  
        if("modify".equals(type)) {
        	LogUtil.logInfo("modify caseinfo  success: ", UserSession.get(Const.LOGINNAME), null, "保存成功!");
        	return ServiceResp.createBySuccessMessage("修改用例库成功!");
		}else {
			LogUtil.logInfo("add caseinfo  success: ", UserSession.get(Const.LOGINNAME), null, "保存成功!");
			return ServiceResp.createBySuccessMessage("保存用例库成功!");
		}
        
    }

    @Override
    public ServiceResp<?> modifyCase(UseCaseLibraryDto useCaseLibraryDto) {
    	
    	// 查找要修改的用例的UUID
        ConcurrentMap<String, UseCaseLibraryDto> caseMap = ConfigManager.getCaseMap();
        UseCaseLibraryDto Dto = caseMap.get(useCaseLibraryDto.getUseCaseLibrary().getCaseName());
        UseCaseLibrary useCaseLibrary = Dto.getUseCaseLibrary();
        String caseName = useCaseLibrary.getCaseName();
        String caseNameUUID = useCaseLibrary.getCaseNameUUID();
        useCaseLibraryDto.getUseCaseLibrary().setCaseNameUUID(caseNameUUID);
        useCaseLibraryDto.getUseCaseLibrary().setTime(new Date());
        List<String> taskCount = new ArrayList<>();
        if (!caseMap.containsKey(caseName)) {
            return ServiceResp.createByErrorMessage("用例库不存在");
        }
        checkTaskStatus(caseName, taskCount);
        if (taskCount.size() != 0) {
            return ServiceResp.createByErrorMessage("有任务关联该用例无法删除");
        }
        String dir = Const.CASE_LIB_PATH + caseNameUUID ;
        String backDir = Const.CASE_LIB_PATH + "tmp";
        File file = new File(dir);
        if (!file.exists() && !file.isDirectory()) {
            return addCase(useCaseLibraryDto);
        }
        ServiceResp serviceResp;
        try {
            // 先备份数据
            FileUtil.copyFolder(dir, backDir);
            File[] files = file.listFiles();
            //用例库引用的用例uuid文件夹下的所有用例名称
            List<String> collect = Arrays.stream(Objects.requireNonNull(files)).map(File::getName).collect(Collectors.toList());
            collect.forEach(col ->
                    FileUtil.deleteFile(dir + File.separator + col)
            );
            serviceResp = addUseCase(useCaseLibraryDto, "modify");
            if (!serviceResp.getSuccess()) {
            	// 回滚数据
            	FileUtil.copyFolder(backDir, dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.copyFolder(backDir, dir);
            LogUtil.logException("modify caseinfo  fail", UserSession.get(Const.LOGINNAME), useCaseLibraryDto, e);
            serviceResp = ServiceResp.createByErrorMessage("修改用例库失败!");
        } finally {
        	FileUtil.deleteDirectory(backDir);
        }
        LogUtil.logInfo("modify caseinfo  success", UserSession.get(Const.LOGINNAME), useCaseLibraryDto, serviceResp.getSuccess());
        return serviceResp;
    }

    @Override
    public ServiceResp<?> delCase(String caseName) {
        // 查询任务状态
        List<String> taskCount = new ArrayList<>();
        checkTaskStatus(caseName, taskCount);
        if (taskCount.size() != 0) {
            return ServiceResp.createByErrorMessage("任务:" + taskCount.toString() + "正在执行,无法修改,请先停止或等待任务结束");
        }
        UseCaseLibraryDto useCaseLibraryDto = ConfigManager.getCaseMap().get(caseName);
        if(useCaseLibraryDto == null) {
        	return ServiceResp.createByErrorMessage("该用例不存在！"); 
        }
        LogUtil.logInfo("useCaseLibraryDto 's val", useCaseLibraryDto, "", "");
        UseCaseLibrary useCaseLibrary = useCaseLibraryDto.getUseCaseLibrary();
        String caseNameUUID = useCaseLibrary.getCaseNameUUID();
        String dir = Const.CASE_LIB_PATH + caseNameUUID;
        String backDir = Const.CASE_LIB_PATH + "tmp";
        File file = new File(dir);
        if (!file.exists() && !file.isDirectory()) {
            ConfigManager.getCaseMap().remove(caseName);
            ConfigManager.genCaseXml();
            return ServiceResp.createBySuccess("删除成功");
        }
        if (useCaseLibraryDto.getReferenceNum() != null &&!"0".equals(useCaseLibraryDto.getReferenceNum())) {
            LogUtil.logFail("del caseinfo  fail ", UserSession.get(Const.LOGINNAME), caseName, "此用例已被配置任务,请先删除任务");
            return ServiceResp.createByErrorMessage("此用例已被配置任务,请先删除任务");
        }
        try {
            FileUtil.copyFolder(dir, backDir);
            //删除用例库
            FileUtil.deleteDirectory(dir);
            ConfigManager.getCaseMap().remove(caseName);
            ConfigManager.genCaseXml();
        } catch (Exception e) {
        	FileUtil.copyFolder(backDir, dir);
            LogUtil.logException("del caseinfo  fail ", UserSession.get(Const.LOGINNAME), caseName, e);
            throw new BizException(e);
        } finally {
            FileUtil.deleteDirectory(backDir);
        }
        LogUtil.logFail("del caseinfo  success ", UserSession.get(Const.LOGINNAME), caseName, "删除成功");
        return ServiceResp.createBySuccess("删除成功");
    }

    @Override
    public ServiceResp<?> getUseCase(String caseName) {
        // 配置文件记录 和 实际检测数据目录比较
        ConcurrentMap<String, UseCaseLibraryDto> caseMap = ConfigManager.getCaseMap();
        ConcurrentMap<String, DetectionTask> detectionMap = ConfigManager.getDetectionMap();
        Collection<DetectionTask> tasks = detectionMap.values();
        for (String key : caseMap.keySet()) {
            List<String> taskList = new ArrayList<>();
            int reference = 0;
            UseCaseLibraryDto useCaseLibraryDto = caseMap.get(key);
            for (DetectionTask detectionTask : tasks) {
                String taskNum = detectionTask.getTaskNum();
                String caseLib = detectionTask.getCaseLib();
                if (caseLib.equals(key)) {
                    taskList.add(taskNum);
                    reference++;
                }
            }
            useCaseLibraryDto.setReferenceTasks(taskList);
            useCaseLibraryDto.setReferenceNum(String.valueOf(reference));
        }
        // 持久配置文件
        ConfigManager.genCaseXml();
        // 判断caseName
        if (caseName == null) {
            Collection<UseCaseLibraryDto> values = caseMap.values();
            List<UseCaseLibraryDto> list = new ArrayList<>(values);
            return ServiceResp.createBySuccess(list);
        } else {
            UseCaseLibraryDto useCaseLibraryDto = caseMap.get(caseName);
            if (useCaseLibraryDto == null) {
                return ServiceResp.createByErrorMessage(caseName + " is not exit");
            } else {
                return ServiceResp.createBySuccess(useCaseLibraryDto);
            }
        }
    }

    private static void checkTaskStatus(String caseName, List<String> taskCount) {
    	 UseCaseLibraryDto useCaseLibraryDto = ConfigManager.getCaseMap().get(caseName);
    	 if(useCaseLibraryDto != null) {    		 
    		 // 查询任务状态
    		 List<String> tasks =useCaseLibraryDto.getReferenceTasks();
    		 if (tasks.size() > 0) {
    			 for (String task : tasks) {
    				 taskCount.add(task);
    			 }
    		 }
    	 }
    }

}
