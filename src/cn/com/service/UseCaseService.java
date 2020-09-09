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
     * 创建用例
     * 1.判断是否存在此用例库
     * 2.首先在cases文件夹下根据用例名称创建文件夹
     * 3.将所选的用例模板复制到cases创建的文件夹下
     * 4.创建文件完成后在cases文件夹下不存在cases.xml则创建并记录本次保存的用例信息
     * @param useCaseLibraryDto 用例库扩展类
     * @return
     */
    ServiceResp<?> addCase(UseCaseLibraryDto useCaseLibraryDto);

    /**
     * 修改用例
     * 1.判断是否存在此用例库
     * 2.首先根据获取到的用例名称与用例文件夹下的文件是否存在相同的，如果有相同的则先删除
     * 3.与创建的2/3步相同
     * 4.reload 用例信息
     * @param useCaseLibraryDto 用例库扩展类
     * @return
     */
    ServiceResp<?> modifyCase(UseCaseLibraryDto useCaseLibraryDto);

    /**
     * 删除用例
     * 1.判断是否存在此用例库
     * 2.判断是否有任务引用
     * 3.删除用例库下的所有文件，并删除文件夹
     * 4.删除cases.xml下制定用例节点
     * 5.reload 用例信息
     * @param caseName 用例库名称
     * @return
     */
    ServiceResp<?> delCase(String caseName);

    /**
     * 获取所有的用例信息
     * @param caseName 用例库名称
     * @return
     */
    ServiceResp<?> getUseCase(String caseName);


}
