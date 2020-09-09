package cn.com.service;

import cn.com.common.ServiceResp;

import cn.com.entity.DetectionTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhaoxin
 * @description
 * @date 2019/8/8 18:21
 */
public interface DetectionTaskService {

    /**
     * @description
     *       查询任务（taskNum为null查询所有）
     *       1.查询配置文件
     *       2.查询detectionData目录任务目录
     *       3.比较封装DetectionTask
     *       4.还要调用工具查询任务状态
     *       5.持久化到配置文件
     * @param taskNum
     * @return ServiceResp<?>
     * @author zhaoxin
     * @date 2019/8/8 19:42
     */
     ServiceResp<?> queryDetectionTask(String taskNum);
     
    /**
     * @description
     *       下载报告前先打包文件
     * @param taskNum
     * @return ServiceResp<?>
     * @author zhaoxin
     * @date 2019/8/8 19:42
     */
    ServiceResp<?> createZipReport(String taskNum);

    /**
     * @description
     *       下载检测报告
     * @param path
     * @return ServiceResp<?>
     * @author zhaoxin
     * @date 2019/8/8 19:42
     */
    void downloadTaskReport(String path, HttpServletRequest request, HttpServletResponse response);

    /**
     * @description
     *       添加任务
     *       1.添加配置（上传的依赖检查是否存在）
     *              依赖相关信息可以从前台获取也可以从后台查询
     *              上传的操作过程要添加之前
     *       2.添加detectionData目录任务目录
     *       3.上传依赖库c 依赖jar（java 必须）及相关文件
     * @param task
     * @return ServiceResp<?>
     * @author zhaoxin
     * @date 2019/8/8 19:42
     */
    ServiceResp<?> addDetectionTask(DetectionTask task);


     /**
      * @description
      *        删除任务
      *        1.从配置文件移除
      *        2.调用任务stop  调用检测工具clean命令
      *        3.删除相关库的目录
      * @param taskNum
      * @return
      * @author zhaoxin
      * @date 2019/8/9 10:57
      */
     ServiceResp<?> deleteDetectionTask(String taskNum);

    /**
     * @description
     *        修改任务
     *        1.修改配置文件
     *        2.上传依赖库c 依赖jar（java 必须）及相关文件
     * @param task
     * @return
     * @author zhaoxin
     * @date 2019/8/9 10:57
     */
    ServiceResp<?> modifyDetectionTask(DetectionTask task);

    /**
     * @description
     *        启动任务
     *        1.判断 到detectionData任务编号目录检测用例
     *        2.判断 启动之前要上传用例 配置init.xml
     *        3.判断 是否正在执行
     * @param taskNum
     * @return
     * @author zhaoxin
     * @date 2019/8/9 10:57
     */
    ServiceResp<?> startTask(String taskNum);

    /**
     * @description
     *        停止任务
              1.调用检测工具stop
     * @param taskNum
     * @return
     */
    ServiceResp<?> stopTask(String taskNum);

    /**
     * @description
     *        重启任务
     *        1.判断 到detectionData任务编号目录检测用例
     *        2.判断 启动之前要上传用例 配置init.xml
     *        3.判断 是否正在执行
     * @param taskNum
     * @return
     */
    ServiceResp<?> restartTask(String taskNum);

    /**
     * @description
     *        清理数据
    1.调用检测工具清理清理数据
     * @return
     */
    ServiceResp<?> cleanData(String taskNum);


  

    /**
     * 校验任务是否存在中间数据
     * 1.路径：/home/cmdFile/NSTestTool/data/taskNum
     * @param taskNum 任务号
     * @return
     */
    ServiceResp<?> checkTask(String taskNum);

}
