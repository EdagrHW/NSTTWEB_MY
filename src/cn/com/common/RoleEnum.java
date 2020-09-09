package cn.com.common;

/**
 * @author wsc
 * @description: ���ֽ�ɫȨ��
 * @date 2019/8/27 18:03
 */
public enum RoleEnum {

    /** �����������Ա**/
    SECURITYADMIN(4,"��ȫ����Ա","security"),
    /** ϵͳ����Ա**/
    SYSADMIN(3,"ϵͳ����Ա","sys"),
    /** �������Ա**/
    TASKADMIN(2,"�������Ա","task"),
    /** ���Ա**/
    VIEWADMIN(1,"���Ա","view");
    /**
     * Ȩ�޵ȼ�
     */
    private int level;
    /**
     * �ȼ�����
     */
    private String desc;
    /**
     * ��Ӧ����ҳ����ת��ַ
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
