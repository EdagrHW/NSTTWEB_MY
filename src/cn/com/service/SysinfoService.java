package cn.com.service;

import cn.com.common.ServiceResp;

/**
 * @descript :  系统信息接口
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
