package cn.com.aop.aspect;

import cn.com.aop.LevelRole;
import cn.com.common.Const;
import cn.com.common.RoleEnum;
import cn.com.common.ServiceResp;
import cn.com.entity.User;
import cn.com.util.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * @author wsc
 * @description
 * @date 2019/8/27 18:11
 */
@Aspect
@Component
public class LevelRoleAspect {

    private static final String PACKAGE_NAME = "ServiceResp";

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Pointcut("execution(* cn.com.infosec.controller..*(..))")
    public void pointCut(){};

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String action = "���Ʋ�ͬȨ�޵��ò�ͬ����";
        MethodSignature signature = (MethodSignature)point.getSignature();
        Method method = signature.getMethod();
        LevelRole adminRole =method.getAnnotation(LevelRole.class);
        if(adminRole==null){
            return point.proceed();
        }
        String className = method.getReturnType().getTypeName();
        if(!className.contains(PACKAGE_NAME)){
            return point.proceed();
        }
        RoleEnum[] roleEnum = adminRole.value();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Const.LOGINNAME);
        // ��Ϊ�������Ѿ���֤���Ƿ�����û����������ǾͲ����ж��Ƿ�Ϊ����
        Integer level =user.getLevel();
        for(RoleEnum item : roleEnum){
            if(item.getLevel()==level){
                return point.proceed();
            }
        }
        LogUtil.logInfo(action,user.getName(),null,"��ֹԽȨ��ʹ�ù���");
        return ServiceResp.createByErrorMessage("��ֹԽȨ��ʹ�ù���");
    }

}
