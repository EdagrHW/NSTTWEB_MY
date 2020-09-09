package cn.com.controller;

import cn.com.aop.LevelRole;
import cn.com.common.RoleEnum;
import cn.com.common.ServiceResp;
import cn.com.entity.dto.UseCaseLibraryDto;
import cn.com.service.UseCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author WangKai
 * @ClassName: UseCaseController
 * @date 2019-08-16 15:26
 * @Description: 用例管理
 */
@Controller
@RequestMapping("/case")
public class UseCaseController {

    @Autowired
    private UseCaseService useCaseService;


    /**
     * @description 获取所有case信息
     * @author wangkai
     * @date 2019/8/22
     */
    @RequestMapping("/query_case")
    @ResponseBody
    @LevelRole({RoleEnum.SECURITYADMIN, RoleEnum.TASKADMIN})
    public ServiceResp<?> getUseCasesList(String caseName){
        return useCaseService.getUseCase(caseName);
    }

    /**
     * @description 添加用例
     * @param useCaseLibraryDto 用例扩展类
     * @author wangkai
     * @date 2019/8/22
     */
    @RequestMapping("/add_case")
    @ResponseBody
    @LevelRole({RoleEnum.SECURITYADMIN, RoleEnum.TASKADMIN})
    public ServiceResp<?> addUseCases(@RequestBody UseCaseLibraryDto useCaseLibraryDto){
        return useCaseService.addCase(useCaseLibraryDto);
    }

    /**
     * @description 修改用例
     * @param useCaseLibraryDto 用例扩展类
     * @author wangkai
     * @date 2019/8/22
     */
    @RequestMapping("/modify_case")
    @ResponseBody
    @LevelRole({RoleEnum.SECURITYADMIN, RoleEnum.TASKADMIN})
    public ServiceResp<?> modifyUseCases(@RequestBody UseCaseLibraryDto useCaseLibraryDto) {
        return useCaseService.modifyCase(useCaseLibraryDto);
    }

    /**
     * @description 删除用例
     * @param caseName 用例名称
     * @author wangkai
     * @date 2019/8/22
     */
    @RequestMapping("/delete_case")
    @ResponseBody
    @LevelRole({RoleEnum.SECURITYADMIN, RoleEnum.TASKADMIN})
    public ServiceResp<?> delUseCases(String caseName) {
        return useCaseService.delCase(caseName);
    }
}
