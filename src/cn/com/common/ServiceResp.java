package cn.com.common;

import java.io.Serializable;

/**
 * 业务响应结果统一对象
 * @ClassName BaseResult
 * @author zhaoxin
 * @Date Apr 9, 2018 1:51:10 PM
 * @version 1.0.0
 */
public final class ServiceResp<T> implements Serializable {

	private static final long serialVersionUID = -4185151304730685014L;

	private boolean success;

    private T data;

    private String msg;
    
    private int status;

    /**
     * 构造方法私有
     * @param status
     * @param success
     */
    private ServiceResp(int status, boolean success){
        this.status=status;
        this.success=success;
    }
    private ServiceResp(int status, String msg, boolean success){
        this.status=status;
        this.msg=msg;
        this.success=success;
    }
    private ServiceResp(int status, T data, boolean success){
        this.status=status;
        this.data=data;
        this.success=success;
    }
    private ServiceResp(int status, String msg, T data, boolean success){
        this.status=status;
        this.msg=msg;
        this.data=data;
        this.success=success;
        
    } 
    public boolean getSuccess(){
        return this.success;
    }

    public int getStatus(){
        return this.status;
    }

    public String getMsg(){
        return this.msg;
    }

    public T getData(){
        return this.data;
    }
    public static  <T> ServiceResp<T> createBySuccess(){
        return new ServiceResp<T>(Const.RespCode.SUCCESS.getCode(), true);
    }
    
    public static <T> ServiceResp<T> createBySuccess(T data){
        return new ServiceResp<T>(Const.RespCode.SUCCESS.getCode(), data, true);
    }
    
    public static <T> ServiceResp<T> createBySuccess(String msg, T data){
        return new ServiceResp<T>(Const.RespCode.SUCCESS.getCode(), msg, data, true);
    }
    
    public static <T> ServiceResp<T> createBySuccessMessage(String msg){
        return  new ServiceResp<T>(Const.RespCode.SUCCESS.getCode(), msg, true);
    }
    
    public static <T> ServiceResp<T> createByError(){
        return new ServiceResp<T>(Const.RespCode.ERROR.getCode(), Const.RespCode.ERROR.getDesc(), false);
    }

    public static <T> ServiceResp<T> createByErrorMessage(String errorMessage){
        return new ServiceResp<T>(Const.RespCode.ERROR.getCode(), errorMessage, false);
    }

    public static <T> ServiceResp<T> createByCodeErrorMessage(int errorCode, String errorMessage){
        return new ServiceResp<T>(errorCode, errorMessage, false);
    }	
}
