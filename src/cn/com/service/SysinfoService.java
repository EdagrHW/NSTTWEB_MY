package cn.com.service;

import cn.com.common.ServiceResp;

/**
 * @descript :  ϵͳ��Ϣ�ӿ�
 *
 * @author : huangwei
 * @date : Create in 2020/09/03
 */
public interface SysinfoService {

     ServiceResp<?> sysUpdate(byte[] data);

     ServiceResp<?> getUpgradeLog();

     ServiceResp<?> viewUpdateLog();

     ServiceResp<?> reboot();
    
}
