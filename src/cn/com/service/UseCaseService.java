package cn.com.service;

import cn.com.common.ServiceResp;
import cn.com.entity.dto.UseCaseLibraryDto;

/**
 * @author WangKai
 * @ClassName: UseCaseService
 * @date 2019-08-20 18:26
 * @Description:
 */
public interface UseCaseService {

    /**
     * ��������
     * 1.�ж��Ƿ���ڴ�������
     * 2.������cases�ļ����¸����������ƴ����ļ���
     * 3.����ѡ������ģ�帴�Ƶ�cases�������ļ�����
     * 4.�����ļ���ɺ���cases�ļ����²�����cases.xml�򴴽�����¼���α����������Ϣ
     * @param useCaseLibraryDto ��������չ��
     * @return
     */
    ServiceResp<?> addCase(UseCaseLibraryDto useCaseLibraryDto);

    /**
     * �޸�����
     * 1.�ж��Ƿ���ڴ�������
     * 2.���ȸ��ݻ�ȡ�������������������ļ����µ��ļ��Ƿ������ͬ�ģ��������ͬ������ɾ��
     * 3.�봴����2/3����ͬ
     * 4.reload ������Ϣ
     * @param useCaseLibraryDto ��������չ��
     * @return
     */
    ServiceResp<?> modifyCase(UseCaseLibraryDto useCaseLibraryDto);

    /**
     * ɾ������
     * 1.�ж��Ƿ���ڴ�������
     * 2.�ж��Ƿ�����������
     * 3.ɾ���������µ������ļ�����ɾ���ļ���
     * 4.ɾ��cases.xml���ƶ������ڵ�
     * 5.reload ������Ϣ
     * @param caseName ����������
     * @return
     */
    ServiceResp<?> delCase(String caseName);

    /**
     * ��ȡ���е�������Ϣ
     * @param caseName ����������
     * @return
     */
    ServiceResp<?> getUseCase(String caseName);


}
