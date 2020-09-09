package cn.com.aop;

import cn.com.common.RoleEnum;

import java.lang.annotation.*;

/**
 * @author wsc
 * @description ֻ��ϵͳ����Ա���ܷ���
 * @date 2019/8/27 17:58
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LevelRole {
    RoleEnum[] value();
}
