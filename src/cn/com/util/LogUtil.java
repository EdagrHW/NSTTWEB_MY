package cn.com.util;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;

public class LogUtil {

	public static Logger logger = Logger.getLogger("nstesttoolweb");

	public static void logInfo(String event, Object user, Object para, Object result){
        logger.info(event+" , user: "+user+" , para£º"+ JSON.toJSONString(para)+", result:"+ JSON.toJSONString(result));
    }
	public static void logSuccess(String event, Object user,Object para){
        logger.info(event +" , user: "+user+" , para£º"+ JSON.toJSONString(para)+", result: success");
    }
	public static void logFail(String event, Object user,Object para){
        logger.info(event+" , user: "+user+" , para£º"+ JSON.toJSONString(para)+", result: fail");
    }
	public static void logFail(String event,Object user, Object para, Object result){
        logger.info(event +" , user: "+user+" , para£º"+ JSON.toJSONString(para)+", result:"+ JSON.toJSONString(result));
    }
	public static void logDebug(String event,Object user, Object para, Object result){
        logger.debug(event +" , user: "+user+" , para£º"+ JSON.toJSONString(para)+", result:"+ JSON.toJSONString(result));
    }
   
	public static void logException(String event,Object user, Object para, Throwable e){
    	logger.error(event +" , user: "+user+" , para:"+ JSON.toJSONString(para)+", result: fail");
    	logger.error(e.getMessage(), e);
    }
}
