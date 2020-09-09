package cn.com.config;

import cn.com.common.Const;
import cn.com.entity.DetectionTask;
import cn.com.entity.User;
import cn.com.entity.dto.UseCaseLibraryDto;
import cn.com.exception.BizException;
import cn.com.interceptor.UserSession;
import cn.com.util.Dom4jUtil;
import cn.com.util.LogUtil;
import cn.com.util.PropertyUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.BaseElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * @author zhaoxin
 * @description 配置文件管理类
 * @date 2019/8/13 10:33
 */
public class ConfigManager {

    /**case管理类*/
    private static ConcurrentMap<String, UseCaseLibraryDto> caseMap = new ConcurrentHashMap<>();
    private static ReadWriteLock caseLock = new ReentrantReadWriteLock();
    /**DetectionTask管理类*/
    private static volatile ConcurrentMap<String, DetectionTask> detectionMap = new ConcurrentHashMap<>();
    /**读写锁*/
    private static ReadWriteLock rwLock = new ReentrantReadWriteLock();
    /**用户管理**/
    public static Map<String, User> userMap = new ConcurrentHashMap<>();
    private static ReadWriteLock userLock = new ReentrantReadWriteLock();

    private static ReadWriteLock cardLock = new ReentrantReadWriteLock();


    /**
     * @description 加载配置文件
     * @author zhaoxin
     * @date 2019/8/13 14:31
     */
    public static void loadConfig() {
        parseDetectionTaskConfig();
        readUserMXL();
        parseCasesConfig();
    }

    private static void traversingFile(List<String> caseList,File file){
        if(file.exists()&&file.isDirectory()){
            File[] files = file.listFiles();
            for (File gmFile:files){
                if(gmFile.isFile()){
                    caseList.add(gmFile.getName());
                }else {
                    traversingFile(caseList,gmFile);
                }
            }
        }
    }

    /**
     * @description 读取cases配置文件
     * @author zhaoxin
     * @date 2019/8/13 10:47
     */
    private static void parseCasesConfig() {
        File file = new File(Const.WEB_CONFIG_PATH+"cases.xml");
        try {
            if (!file.exists() && !file.isFile()) {
                file.createNewFile();
                Document document = Dom4jUtil.createXml();
                Dom4jUtil.addChild(document, "cases-config");
                FileUtil.writeString(Dom4jUtil.toStr2(document), file, "UTF-8");
            }
            rwLock.readLock().lock();
            Document document = Dom4jUtil.readXML(file);
            Element root = document.getRootElement();
            Iterator<Element> it = root.elementIterator();
            while (it.hasNext()) {
                Element item = it.next();
                UseCaseLibraryDto useCaseLibraryDto = new UseCaseLibraryDto();
                useCaseLibraryDto.parse(item);
                caseMap.put(useCaseLibraryDto.getUseCaseLibrary().getCaseName(), useCaseLibraryDto);
            }
        } catch (Exception ex) {
            LogUtil.logException("parse cases.xml", UserSession.get(Const.LOGINNAME), null, ex);
            throw new BizException("parse cases.xml fail:" + ex.getMessage());
        } finally {
            rwLock.readLock().unlock();
        }

    }

    /**
     * @description 读取detectionTask配置文件
     * @author zhaoxin
     * @date 2019/8/13 10:47
     */
    public static void parseDetectionTaskConfig() {
        try {
            File file = new File(Const.WEB_CONFIG_PATH+"detectionTask.xml");
            if (!file.exists() || !file.isFile() || file.length() <= 0) {
                file.createNewFile();
                Document document = Dom4jUtil.createXml();
                Dom4jUtil.addChild(document, "detection-config");
                FileUtil.writeString(Dom4jUtil.toStr2(document), file, "UTF-8");
                //throw new BizException("load detectionTask.xml fail {} do not exit", DETECTION_PATH);
            }
            rwLock.readLock().lock();
            Document document = Dom4jUtil.readXML(file);
            Element root = document.getRootElement();
            Iterator<Element> it = root.elementIterator();
            while (it.hasNext()) {
                Element item = it.next();
                DetectionTask detectionTask = new DetectionTask();
                detectionTask.parse(item);
                detectionMap.put(detectionTask.getTaskNum(), detectionTask);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.logException("parse detectionTask.xml", null, null, ex);
            throw new BizException("parse detectionTask.xml fail:" + ex.getMessage());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * @description 生成detectionTask配置文件
     * @author zhaoxin
     * @date 2019/8/13 10:54
     */
    public static void genDetectionTaskXml() {
        // 备份逻辑
        File originalFile = new File(Const.WEB_CONFIG_PATH+"detectionTask.xml");
        File bakFile = new File(Const.WEB_CONFIG_PATH+"detectionTask.xml" + ".bak");
        if (bakFile.exists()) {
            bakFile.delete();
        }
        if (originalFile.exists() && !originalFile.renameTo(bakFile)) {
            try (FileInputStream fis = new FileInputStream(originalFile);) {
                FileOutputStream fos = new FileOutputStream(bakFile);
                IoUtil.copy(fis, fos);
                IoUtil.close(fis);
                IoUtil.close(fos);
            } catch (Exception ex) {
                LogUtil.logException("backup detectionTask.xml", null, null, ex);
            }
        }
        // 创建一个输出流对象
        try {
            rwLock.writeLock().lock();
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("detection-config");
            for (Map.Entry<String, DetectionTask> item : detectionMap.entrySet()) {
                DetectionTask detectionTask = item.getValue();
                detectionTask.toXml(root);
            }
            Dom4jUtil.toFile(doc,Const.WEB_CONFIG_PATH+"detectionTask.xml");
        } catch (Exception e) {
            LogUtil.logException("gen detectionTask.xml", null, null, e);
            //回滚逻辑
            if (bakFile.exists()) {
                try (FileInputStream fis = new FileInputStream(bakFile)) {
                    FileOutputStream fos = new FileOutputStream(originalFile);
                    IoUtil.copy(fis, fos);
                } catch (Exception ex) {
                    LogUtil.logException("roll detectionTask.xml", null, null, ex);
                }
            }
            detectionMap.clear();
            parseDetectionTaskConfig();
            throw new BizException("gen detectionTask.xml fail");
        } finally {
            rwLock.writeLock().unlock();
        }
    }


    /**
     * @description 生成cases配置文件
     * @author wangkai
     * @date 2019/8/29 18:26
     */
    public static void genCaseXml() {
        // 备份逻辑
        File originalFile = new File(Const.WEB_CONFIG_PATH+"cases.xml");
        File bakFile = new File(Const.WEB_CONFIG_PATH+"cases.xml" + ".bak");
        if (bakFile.exists()) {
            bakFile.delete();
        }
        if (originalFile.exists() && !originalFile.renameTo(bakFile)) {
            try (FileInputStream fis = new FileInputStream(originalFile)) {
                FileOutputStream fos = new FileOutputStream(bakFile);
                IoUtil.copy(fis, fos);
                IoUtil.close(fos);
                IoUtil.close(fis);
            } catch (Exception ex) {
                ex.printStackTrace();
                LogUtil.logException("backup cases.xml", null, null, ex);
            }
        }
        // 创建一个输出流对象
        try  {
            caseLock.writeLock().lock();
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("cases-config");
            for (Map.Entry<String, UseCaseLibraryDto> item : caseMap.entrySet()) {
                UseCaseLibraryDto useCaseLibraryDto = item.getValue();
                useCaseLibraryDto.toXml(root);
            }
            Dom4jUtil.toFile(doc,Const.WEB_CONFIG_PATH+"cases.xml");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logException("gen cases.xml", null, null, e);
            //回滚逻辑
            if (bakFile.exists()) {
                try (FileInputStream fis = new FileInputStream(bakFile)) {
                    FileOutputStream fos = new FileOutputStream(originalFile);
                    IoUtil.copy(fis, fos);
                    IoUtil.close(fos);
                    IoUtil.close(fis);
                } catch (Exception ex) {
                    LogUtil.logException("roll cases.xml", null, null, ex);
                }
            }
            userMap.clear();
            parseCasesConfig();
            throw new BizException("gen cases.xml fail");
        } finally {
            caseLock.writeLock().unlock();
        }
    }
    public static ConcurrentMap<String, DetectionTask> getDetectionMap() {
        return detectionMap;
    }

    public static ConcurrentMap<String, UseCaseLibraryDto> getCaseMap() {
        return caseMap;
    }


    /**
     * 读取存储用户列表的xml文件
     */
    public static void readUserMXL() {
        userLock.readLock().lock();
        try {
            userMap.clear();
            File useFile = new File(Const.USER_CONFIG);
            if (!useFile.exists() || !useFile.isFile()) {
                useFile.createNewFile();
                Document xml = Dom4jUtil.createXml();
                //user-config
                Dom4jUtil.addChild(xml, "user-config");
                FileUtil.writeString(Dom4jUtil.toStr(xml), useFile, "UTF-8");
            }
            SAXReader reader = new SAXReader();
            Document doc = reader.read(Const.USER_CONFIG);
            Element root = doc.getRootElement();
            Iterator<Element> iterator = root.elementIterator("user");
            while (iterator.hasNext()) {
                Element element = iterator.next();
                User user = new User();
                user.setId(element.elementTextTrim("uuid"));
                String name = element.elementTextTrim("name");
                user.setName(name);
                user.setPassword(element.elementTextTrim("password"));
                user.setLevel(Integer.parseInt(element.elementTextTrim("level")));
                userMap.put(name, user);
            }
        } catch (Exception e) {
            LogUtil.logException("load userconfig.xml fail", UserSession.get(Const.LOGINNAME), null, e);
            throw new BizException("load userconfig.xml fail");
        } finally {
            userLock.readLock().unlock();
        }

    }

   


    /**
     * 把新增用户写入到xml文件中
     */
    public static void writerUserXML() {
        userLock.writeLock().lock();
        File file = new File(Const.USER_CONFIG);
        File bakFile = new File(Const.USER_CONFIG + ".bak");
        backupFIle(file, bakFile, "备份userconfig xml");
        XMLWriter xw = null;
        try {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("user-config");
            for (Map.Entry<String, User> map : userMap.entrySet()) {
                addChildElement(root, map.getValue());
            }
            Dom4jUtil.toFile(doc,Const.USER_CONFIG);
        } catch (Exception e) {
            backupFIle(bakFile, file, "userconfig.xml 回滚");
            LogUtil.logException("wirter userConfig fail", null, null, e);
            throw new BizException(e.getMessage());
        } finally {
            try {
                if (xw != null) {
                    xw.close();
                }
            } catch (Exception e) {
            }
            userLock.writeLock().unlock();
        }
    }

  

    private static void addChildElement(Element root, User user) {
        Element child = new BaseElement("user");
        child.addElement("uuid").addText(user.getId());
        child.addElement("name").addText(user.getName());
        child.addElement("password").addText(user.getPassword());
        child.addComment("1代表审计员，2代表操作管理员，3代表系统管理员");
        child.addElement("level").addText(Integer.toString(user.getLevel()));
        root.add(child);
    }



    /**
     * 文件的重命名
     */
    public static boolean backupFIle(File originFile, File backFile, String tips) {
        boolean flag = false;
        if (backFile.exists()) {
            backFile.delete();
        }
        if (originFile.exists() && !originFile.renameTo(backFile)) {
            try (FileInputStream fis = new FileInputStream(originFile);
                 FileOutputStream fos = new FileOutputStream(backFile);) {
                IoUtil.copy(fis, fos);
                flag = true;
            } catch (IOException e) {
                if (StrUtil.isBlank(tips)) {
                    tips = "backup file fail";
                }
                LogUtil.logException(tips, UserSession.get(Const.LOGINNAME), null, e);
                flag = false;
            }
        } else {
            flag = true;
        }
        return flag;
    }

    /**
     * 如果userMap为空则重新在xml文件中查找
     * 防止失败后造成的一系列错误
     */
    public static Map<String, User> getUserMap() {
        if (userMap.size() == 0) {
            readUserMXL();
        }
        return userMap;
    }

  
}
