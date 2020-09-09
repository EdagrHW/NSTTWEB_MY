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
     *       ��ѯ����taskNumΪnull��ѯ���У�
     *       1.��ѯ�����ļ�
     *       2.��ѯdetectionDataĿ¼����Ŀ¼
     *       3.�ȽϷ�װDetectionTask
     *       4.��Ҫ���ù��߲�ѯ����״̬
     *       5.�־û��������ļ�
     * @param taskNum
     * @return ServiceResp<?>
     * @author zhaoxin
     * @date 2019/8/8 19:42
     */
     ServiceResp<?> queryDetectionTask(String taskNum);
     
    /**
     * @description
     *       ���ر���ǰ�ȴ���ļ�
     * @param taskNum
     * @return ServiceResp<?>
     * @author zhaoxin
     * @date 2019/8/8 19:42
     */
    ServiceResp<?> createZipReport(String taskNum);

    /**
     * @description
     *       ���ؼ�ⱨ��
     * @param path
     * @return ServiceResp<?>
     * @author zhaoxin
     * @date 2019/8/8 19:42
     */
    void downloadTaskReport(String path, HttpServletRequest request, HttpServletResponse response);

    /**
     * @description
     *       �������
     *       1.������ã��ϴ�����������Ƿ���ڣ�
     *              ���������Ϣ���Դ�ǰ̨��ȡҲ���ԴӺ�̨��ѯ
     *              �ϴ��Ĳ�������Ҫ���֮ǰ
     *       2.���detectionDataĿ¼����Ŀ¼
     *       3.�ϴ�������c ����jar��java ���룩������ļ�
     * @param task
     * @return ServiceResp<?>
     * @author zhaoxin
     * @date 2019/8/8 19:42
     */
    ServiceResp<?> addDetectionTask(DetectionTask task);


     /**
      * @description
      *        ɾ������
      *        1.�������ļ��Ƴ�
      *        2.��������stop  ���ü�⹤��clean����
      *        3.ɾ����ؿ��Ŀ¼
      * @param taskNum
      * @return
      * @author zhaoxin
      * @date 2019/8/9 10:57
      */
     ServiceResp<?> deleteDetectionTask(String taskNum);

    /**
     * @description
     *        �޸�����
     *        1.�޸������ļ�
     *        2.�ϴ�������c ����jar��java ���룩������ļ�
     * @param task
     * @return
     * @author zhaoxin
     * @date 2019/8/9 10:57
     */
    ServiceResp<?> modifyDetectionTask(DetectionTask task);

    /**
     * @description
     *        ��������
     *        1.�ж� ��detectionData������Ŀ¼�������
     *        2.�ж� ����֮ǰҪ�ϴ����� ����init.xml
     *        3.�ж� �Ƿ�����ִ��
     * @param taskNum
     * @return
     * @author zhaoxin
     * @date 2019/8/9 10:57
     */
    ServiceResp<?> startTask(String taskNum);

    /**
     * @description
     *        ֹͣ����
              1.���ü�⹤��stop
     * @param taskNum
     * @return
     */
    ServiceResp<?> stopTask(String taskNum);

    /**
     * @description
     *        ��������
     *        1.�ж� ��detectionData������Ŀ¼�������
     *        2.�ж� ����֮ǰҪ�ϴ����� ����init.xml
     *        3.�ж� �Ƿ�����ִ��
     * @param taskNum
     * @return
     */
    ServiceResp<?> restartTask(String taskNum);

    /**
     * @description
     *        ��������
    1.���ü�⹤��������������
     * @return
     */
    ServiceResp<?> cleanData(String taskNum);


  

    /**
     * У�������Ƿ�����м�����
     * 1.·����/home/cmdFile/NSTestTool/data/taskNum
     * @param taskNum �����
     * @return
     */
    ServiceResp<?> checkTask(String taskNum);

}
