package cn.com.common;

/**
 * @author wsc
 * @description: 各种角色权限
 * @date 2019/8/27 18:03
 */
public enum RoleEnum {

    /** 超级任务管理员**/
    SECURITYADMIN(4,"安全管理员","security"),
    /** 系统管理员**/
    SYSADMIN(3,"系统管理员","sys"),
    /** 任务管理员**/
    TASKADMIN(2,"任务管理员","task"),
    /** 审计员**/
    VIEWADMIN(1,"审计员","view");
    /**
     * 权限等级
     */
    private int level;
    /**
     * 等级介绍
     */
    private String desc;
    /**
     * 对应的主页面跳转地址
     */
    private String href;

    RoleEnum(int level,String desc,String href){
        this.level = level;
        this.desc = desc;
    }

    public int getLevel(){
        return this.level;
    }
    public String getDesc(){
        return this.desc;
    }
    public String getHref(){return this.href;}
}
