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
 * @description ʵ����
 * @date 2019/8/12 18:17
 */
@Service
public class DetectionTaskServiceImpl implements DetectionTaskService {
    /**
     * ����״̬ û�б����¼�ҵ��ù���status����ֵΪ-1
     */
    private static final int STATE_READY = -1;
    /**
     * ������
     */
    private static final int STATE_RUNNING = 0;

    /**
     * δ����
     */
    private static final int STATE_NOT_RUNNING = 1;
    /**
     * �����쳣
     */
    private static final int STATE_ERROR = 2;


    /**
     * ����״̬ ����Ĭ��5��
     */
    private static final TimedCache<String, Integer> STATE_CACHE = new TimedCache<>(1000 * 5);

    /**
     * ����jar ����Ĭ��30��
     */
    private static final TimedCache<String, List<String>> LIB_CACHE = new TimedCache<>(1000 * 60);
    /**
     * ����c�� ����Ĭ��30��
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
    	 return ServiceResp.createBySuccess("���ر���ɹ�");
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
                // 4.д�������(out)��
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
        // �ж������
        String taskNum = task.getTaskNum();
        if ("".equals(taskNum) || taskNum == null) {
            LogUtil.logFail("add detection task fail", UserSession.get(Const.LOGINNAME), task, "����Ų���Ϊ��");
            return ServiceResp.createByErrorMessage("����Ų���Ϊ��");
        }
        if (ConfigManager.getDetectionMap().get(taskNum) != null && !"modify".equals(type)) {
            LogUtil.logFail("add detection task fail", UserSession.get(Const.LOGINNAME), task, "�������Ѵ���,�����ٴδ���");
            return ServiceResp.createByErrorMessage("�������Ѵ���,�����ٴδ���");
        }

        File file = new File(Const.DETECTION_DATA_PATH + taskNum);
        if (file.exists() && file.isDirectory()) {
            LogUtil.logFail("add detection task fail", UserSession.get(Const.LOGINNAME), task, "�������ѱ�����");
            return ServiceResp.createByErrorMessage("�������ѱ�����");
        }
        String caselib = task.getCaseLib();
        UseCaseLibraryDto useCaseLibraryDto = ConfigManager.getCaseMap().get(caselib);
        String caseNameUUID = useCaseLibraryDto.getUseCaseLibrary().getCaseNameUUID();
        String libPath = Const.CASE_LIB_PATH + caseNameUUID;
        File libFile = new File(libPath);
        if (!libFile.exists() || !libFile.isDirectory()) {
            LogUtil.logFail("add detection task fail", UserSession.get(Const.LOGINNAME), task, "�����ⲻ����");
            return ServiceResp.createByErrorMessage("�����ⲻ����");
        } else if (libFile.list() == null || Objects.requireNonNull(libFile.list()).length < 1) {
            LogUtil.logFail("add detection task fail", UserSession.get(Const.LOGINNAME), task, "�����������޷�ʹ��,������ѡ��������");
            return ServiceResp.createByErrorMessage("�����������޷�ʹ��,������ѡ��������");
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
     * ɾ������
     * 1.�������ļ��Ƴ�
     * 2.��������stop  ���ü�⹤��clean����
     * 3.ɾ����ؿ��Ŀ¼
     *
     * @param taskNum �����
     */
    @Override
    public ServiceResp<?> deleteDetectionTask(String taskNum) {
        // ��ѯ����״̬
        String rootPath = Const.DETECTION_DATA_PATH + taskNum;
        File taskFile = new File(rootPath);
        if (!taskFile.exists() && !taskFile.isDirectory()) {
            ConfigManager.getDetectionMap().remove(taskNum);
            ConfigManager.genDetectionTaskXml();
            return ServiceResp.createBySuccess("ɾ���ɹ�");
        }
        
        ConcurrentMap<String, DetectionTask> detectionMap = ConfigManager.getDetectionMap();
        
        if (detectionMap.get(taskNum).getState() != STATE_RUNNING) {
            try {
            	detectionMap.remove(taskNum);
                cn.com.util.FileUtil.deleteDirectory(rootPath);
                ConfigManager.genDetectionTaskXml();
                //��Ҫɾ����������ļ���
                LogUtil.logInfo("del detection task success", UserSession.get(Const.LOGINNAME), taskNum, null);
                return ServiceResp.createBySuccess("����ɾ���ɹ�");
            } catch (Exception e) {
                LogUtil.logException("del detection task fail fail", UserSession.get(Const.LOGINNAME), taskNum, e);
                return ServiceResp.createByErrorMessage("����ɾ��ʧ��");
            }
        } else {
            return ServiceResp.createByErrorMessage("���������������޷�ɾ��");
        }
    }

    @Override
    public ServiceResp<?> modifyDetectionTask(DetectionTask task) {
        // �ж������
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
            //�滻������
            String caselib = task.getCaseLib();
            cn.com.util.FileUtil.deleteDirectory(path + File.separator + "testCase");
            String caseNameUUID = ConfigManager.getCaseMap().get(caselib).getUseCaseLibrary().getCaseNameUUID();
            String libPath = Const.CASE_LIB_PATH + caseNameUUID;
            String testCasePath = path + File.separator + "testCase";
            cn.com.util.FileUtil.copyFolder(libPath, testCasePath);
            //�޸�detectionTask.xml
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
     * ��������
     * 1.�ж� ��detectionData������Ŀ¼�������
     * 2.�ж� ����֮ǰҪ�ϴ����� ����init.xml
     * 3.�ж� �Ƿ�����ִ��
     *
     * @param taskNum �����
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
            return ServiceResp.createByErrorMessage("û���������޷�����");
        } else if (ConfigManager.getCaseMap().get(caseLib).getUseCaseLibrary() == null) {
            return ServiceResp.createByErrorMessage("û���������޷�����");
        }
        List<String> casesName = ConfigManager.getCaseMap().get(caseLib).getUseCaseLibrary().getCasesName();
        if (casesName.isEmpty()) {
            LogUtil.logInfo("startTask detection task fail", UserSession.get(Const.LOGINNAME), taskNum, "ȱ�ٱ�Ҫ���������޷�����,�����޸ĺ��ٴγ��Կ���");
            return ServiceResp.createByErrorMessage("������������������,�����޸ĺ��ٴγ��Կ���");
        }

        File taskFile = new File(rootPath);
        if (!taskFile.exists() || !taskFile.isDirectory()) {
            LogUtil.logInfo("startTask detection task fail", UserSession.get(Const.LOGINNAME), taskNum, "���������޷�ִ��,�����޸ĺ��ٴ�ִ��");
            return ServiceResp.createByErrorMessage("���������޷�ִ��,�����޸ĺ��ٴ�ִ��");
        }
        
        ConfigManager.getDetectionMap().get(taskNum).setLastModifyDate(new Date());
        ConfigManager.getDetectionMap().get(taskNum).setLastDate(new Date());
        ConfigManager.genDetectionTaskXml();
        LogUtil.logInfo("startTask detection task success", UserSession.get(Const.LOGINNAME), taskNum, null);
        return ServiceResp.createBySuccess("���������ɹ�");
    }

    @Override
    public ServiceResp<?> stopTask(String taskNum) {
        ServiceResp<?> serviceResp = checkTaskIsOk(taskNum);
        if (!serviceResp.getSuccess()) {
            LogUtil.logInfo("stopTask detection task fail", UserSession.get(Const.LOGINNAME), taskNum, serviceResp);
            return serviceResp;
        }
        return ServiceResp.createBySuccess("����ֹͣ�ɹ�");
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
            return ServiceResp.createByErrorMessage("û���������޷�����");
        } else if (ConfigManager.getCaseMap().get(caseLib).getUseCaseLibrary() == null) {
            return ServiceResp.createByErrorMessage("û���������޷�����");
        }
        List<String> casesName = ConfigManager.getCaseMap().get(caseLib).getUseCaseLibrary().getCasesName();
        if (casesName.isEmpty()) {
            LogUtil.logInfo("restartTask detection task fail", UserSession.get(Const.LOGINNAME), taskNum, "ȱ�ٱ�Ҫ���������޷�����,�����޸ĺ��ٴγ��Կ���");
            return ServiceResp.createByErrorMessage("������������������,�����޸ĺ��ٴγ��Կ���");
        }

        File taskFile = new File(rootPath);
        if (!taskFile.exists() || !taskFile.isDirectory()) {
            LogUtil.logInfo("restartTask detection task fail", UserSession.get(Const.LOGINNAME), taskNum, "���������޷�ִ��,�����޸ĺ��ٴ�ִ��");
            return ServiceResp.createByErrorMessage("���������޷�ִ��,�����޸ĺ��ٴ�ִ��");
        }
        ConfigManager.getDetectionMap().get(taskNum).setLastModifyDate(new Date());
        ConfigManager.getDetectionMap().get(taskNum).setLastDate(new Date());
        ConfigManager.genDetectionTaskXml();
        LogUtil.logInfo("restartTask detection task success", UserSession.get(Const.LOGINNAME), taskNum, null);
        return ServiceResp.createBySuccess("���������ɹ�");
    }


    @Override
    public ServiceResp<?> cleanData(String taskNum) {
    	
    	String logPath = Const.SSLVPN_LOG_PATH + taskNum;
    	cn.com.util.FileUtil.deleteDirectory(logPath);
    	return ServiceResp.createBySuccess("��������ɹ�");
    }

    private ServiceResp<?> checkTaskIsOk(String taskNum) {
        Map<String, Object> map;
        String rootPath = Const.DETECTION_DATA_PATH + taskNum;
        File file = new File(rootPath);
        if (!file.exists() || !file.isDirectory()) {
            LogUtil.logInfo("checkTaskIsOk detection task fail", UserSession.get(Const.LOGINNAME), taskNum, "���񲻴���");
            return ServiceResp.createByErrorMessage("���񲻴���");
        }
        return ServiceResp.createBySuccess("����OK");
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
