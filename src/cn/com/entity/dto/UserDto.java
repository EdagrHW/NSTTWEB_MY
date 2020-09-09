package cn.com.entity.dto;

import javax.validation.constraints.NotBlank;

import com.alibaba.fastjson.annotation.JSONField;

public class UserDto {
	  private String id;

	    @NotBlank(message = "用户名不能为空")
	    private String name;

	    @NotBlank(message = "密码不能为空")
	    @JSONField(serialize = false) // 查询时忽略该字段
	    private  String password;


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




		@Override
		public String toString() {
			return "User [id=" + id + ", name=" + name + ", password=" + password + "]";
		}
	    
}
