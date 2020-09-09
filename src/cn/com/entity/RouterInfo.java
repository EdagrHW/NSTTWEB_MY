package cn.com.entity;

import java.util.List;

public class RouterInfo {
	private String authName;
	private String path;
	private Integer id;
	private List<RouterInfo> children;
	public String getAuthName() {
		return authName;
	}
	public void setAuthName(String authName) {
		this.authName = authName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<RouterInfo> getChildren() {
		return children;
	}
	public void setChildren(List<RouterInfo> children) {
		this.children = children;
	}
	@Override
	public String toString() {
		return "RouterInfo [authName=" + authName + ", path=" + path + ", id=" + id + ", children=" + children + "]";
	}
	public RouterInfo(String authName, String path, Integer id) {
		super();
		this.authName = authName;
		this.path = path;
		this.id = id;
	}
	public RouterInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
