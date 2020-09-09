package cn.com.interceptor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WangKai
 * @ClassName: UserSession
 * @date 2019-08-21 18:18
 * @Description: ��ThreadLocal�ṩһ���洢�߳��ڱ����ĵط�. <p/>
 * �ͻ��˴�������þ�̬�����洢�ͻ�ȡ�߳��ڱ���,����Ҫ������HttpSession.
 * web���Controller��ͨ���˴���business�㴫��user_id֮��ı���
 */

@SuppressWarnings("unchecked")
public class UserSession {

    /** * ���������ThreadLocal��������ͬһ�߳���ͬ������. */
    private static final ThreadLocal SESSION_MAP = new ThreadLocal();

    /** * �������protected���췽��. */
    protected UserSession() {
    }

    /**
     * ����߳��б��������.
     *
     * @param attribute
     *            ��������
     * @return ����ֵ
     */
    public static Object get(String attribute) {
        Map map = (Map) SESSION_MAP.get();

        return map.get(attribute);
    }

    /**
     * ����߳��б�������ԣ�ʹ��ָ�����ͽ���ת��.
     *
     * @param attribute
     *            ��������
     * @param clazz
     *            ����
     * @param <T>
     *            �Զ�ת��
     * @return ����ֵ
     */
    public static <T> T get(String attribute, Class<T> clazz) {
        return (T) get(attribute);
    }

    /**
     * �����ƶ���������ֵ.
     *
     * @param attribute
     *            ��������
     * @param value
     *            ����ֵ
     */
    public static void set(String attribute, Object value) {
        Map map = (Map) SESSION_MAP.get();
        if (map == null) {
            map = new HashMap();
            SESSION_MAP.set(map);
        }
        map.put(attribute, value);
    }
    /**
     * �˳���¼�����SESSION_MAP
     */
    public static void remove() {
        SESSION_MAP.remove();
    }

}
