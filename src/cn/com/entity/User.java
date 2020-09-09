package cn.com.entity;


import com.alibaba.fastjson.annotation.JSONField;

import javax.validation.constraints.NotBlank;

/**
 * @Author: ��˳��
 * @Description:  �������û�������
 * @Date: Create in 17:35 2019/8/16
 */
public class User {

    private String id;

    @NotBlank(message = "�û�������Ϊ��")
    private String name;

    @NotBlank(message = "���벻��Ϊ��")
    @JSONField(serialize = false) // ��ѯʱ���Ը��ֶ�
    private  String password;

    @NotBlank(message = "Ȩ�޵ȼ�����Ϊ��")
    private Integer level;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + password + ", level=" + level + "]";
	}
    
    


}
