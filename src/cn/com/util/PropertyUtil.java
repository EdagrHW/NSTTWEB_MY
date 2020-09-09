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
 * @Description: .properties�����ļ�����������
 */
public class PropertyUtil {

   // private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);

    /** .properties�����ļ�����׺ */
    public static final String PROPERTY_FILE_SUFFIX	= ".properties";

    /**
     * ���������ļ���,��ȡ����
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
                /* �����ļ�����׺ */
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
            //LOGGER.error(e,"���������ļ�����!");
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
                /* �����ļ�����׺ */
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
            //LOGGER.error(e,"���������ļ�����!");
            throw new RuntimeException(e);
        }
        return properties;
    }
    /**
     * ����key,��ȡ����ֵ
     *
     * @param properties
     * @param key
     * @return
     */
    public static String getString(Properties properties, String key){
        return properties.getProperty(key);
    }

    /**
     * ����key,��ȡ����ֵ
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
     * ����key,��ȡ����ֵ
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
