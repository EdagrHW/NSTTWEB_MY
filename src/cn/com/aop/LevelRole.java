package cn.com.aop;

import cn.com.common.RoleEnum;

import java.lang.annotation.*;

/**
 * @author wsc
 * @description 只有系统管理员才能访问
 * @date 2019/8/27 17:58
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LevelRole {
    RoleEnum[] value();
}
