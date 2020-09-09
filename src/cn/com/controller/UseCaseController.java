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
 * @Description: ��������
 */
@Controller
@RequestMapping("/case")
public class UseCaseController {

    @Autowired
    private UseCaseService useCaseService;


    /**
     * @description ��ȡ����case��Ϣ
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
     * @description �������
     * @param useCaseLibraryDto ������չ��
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
     * @description �޸�����
     * @param useCaseLibraryDto ������չ��
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
     * @description ɾ������
     * @param caseName ��������
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
