package cn.com.util;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author WangKai
 * @ClassName: PropertyUtil
 * @date 2019-03-11 14:05
 * @Description: .properties属性文件操作工具类
 */
public class PropertyUtil {

   // private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);

    /** .properties属性文件名后缀 */
    public static final String PROPERTY_FILE_SUFFIX	= ".properties";

    /**
     * 根据属性文件名,获取属性
     *
     * @param propsFileName
     * @return
     */
    public static Properties getProperties(String propsFileName) {
        if (StringUtils.isEmpty(propsFileName)) {
            throw new IllegalArgumentException();
        }
        Properties  properties  = new Properties();
        InputStream inputStream = null;
        try {
            try {
                /* 加入文件名后缀 */
                if (propsFileName.lastIndexOf(PROPERTY_FILE_SUFFIX) == -1) {
                    propsFileName += PROPERTY_FILE_SUFFIX;
                }
                inputStream = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream(propsFileName);
                if (null != inputStream) {
                    properties.load(inputStream);
                }
            } finally {
                if ( null != inputStream) {
                    inputStream.close();
                }
            }

        } catch (IOException e) {
            //LOGGER.error(e,"加载属性文件出错!");
            throw new RuntimeException(e);
        }

        return properties;
    }

    public static Properties getProperty(String propsFilePath) {
        if (StringUtils.isEmpty(propsFilePath)) {
            throw new IllegalArgumentException();
        }
        Properties  properties  = new Properties();
        InputStream inputStream = null;
        try {
            try {
                /* 加入文件名后缀 */
                if (propsFilePath.lastIndexOf(PROPERTY_FILE_SUFFIX) == -1) {
                    propsFilePath += PROPERTY_FILE_SUFFIX;
                }
               File file=new File(propsFilePath);
                inputStream = new FileInputStream(file);
                properties.load(inputStream);
            } finally {
                if ( null != inputStream) {
                    inputStream.close();
                }
            }

        } catch (IOException e) {
            //LOGGER.error(e,"加载属性文件出错!");
            throw new RuntimeException(e);
        }
        return properties;
    }
    /**
     * 根据key,获取属性值
     *
     * @param properties
     * @param key
     * @return
     */
    public static String getString(Properties properties, String key){
        return properties.getProperty(key);
    }

    /**
     * 根据key,获取属性值
     *
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getStringOrDefault(Properties properties, String key, String defaultValue){
        return properties.getProperty(key,defaultValue);
    }

    /**
     * 根据key,获取属性值
     *
     * @param properties
     * @param key
     * @param defaultValue
     * @param <V>
     * @return
     */
    public static <V> V getOrDefault(Properties properties, String key, V defaultValue){
        return (V) properties.getOrDefault(key,defaultValue);
    }
}
