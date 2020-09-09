package cn.com.service.impl;

import cn.com.common.Const;
import cn.com.common.ServiceResp;
import cn.com.service.SysinfoService;
import cn.com.util.FileUtil;
import cn.com.util.LogUtil;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * @Author: zhouhuang
 * @Description:
 * @Date: Create in 13:46 2019/8/15
 */
@Service
public class SysinfoServiceImpl implements SysinfoService {


    /**
     * @description
     * @param data
     * @return ServiceResp<?>
     * @author zhouhuang
     * @date 2019/12/3 10:35
     */
    @Override
    public ServiceResp<?> sysUpdate(byte[] data) {
        String action = "系统升级";
        //如果文件夹不存在，则新建文件夹
        FileUtil.createNewFileDir(Const.SYSTEM_SCRIPT_PATH);
        String updateFilePath = Const.SYSTEM_SCRIPT_PATH + "upgrade.bin";
        BufferedOutputStream bos = null;
        try{
            FileOutputStream fos =  new FileOutputStream(updateFilePath);
            bos = new BufferedOutputStream(fos);
            bos.write(data);
            bos.flush();
        }catch (Exception e){
            LogUtil.logException(action,null,"data length"+data.length,e);
            return ServiceResp.createByErrorMessage("系统升级文件上传失败!");
        }finally {
            if(bos!=null){
                try {
                    bos.close();
                }catch (IOException e){
                }
            }
        }
        File dir = new File(Const.SYSTEM_SCRIPT_PATH);
        return ServiceResp.createByErrorMessage("升级成功");
    }

    /**
     * @description
     * @return ServiceResp<?>
     * @author zhouhuang
     * @date 2019/12/3 10:35
     */
    @Override
    public ServiceResp<?> getUpgradeLog(){
        Reader fr = null;
        BufferedReader br = null;
        List<String> list = new ArrayList<String>();
        try {
            fr = new FileReader(Const.SSLVPN_LOG_PATH + "system.log");
            br = new BufferedReader(fr);
            String line = null;
            while((line=br.readLine())!=null){
                list.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 3.关闭流
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ServiceResp.createBySuccess(list);
    }

    /**
     * @description
     * @return ServiceResp<?>
     * @author zhouhuang
     * @date 2019/12/3 10:35
     */
    @Override
    public ServiceResp<?> viewUpdateLog() {
        String[] resultArr = {"11111111111111\n", "222222222222222222\n", "33333333333333\n"};
        return ServiceResp.createBySuccess(new ArrayList<>(Arrays.asList(resultArr)));
    }

    /**
     * @description
     * @return ServiceResp<?>
     * @author zhouhuang
     * @date 2019/12/3 10:35
     */
    @Override
    public ServiceResp<?> reboot() {
    	return ServiceResp.createBySuccess("success");
    }


}
