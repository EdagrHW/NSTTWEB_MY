package cn.com.common;

import java.io.File;

/**
 * ������
 * 
 * @author zhaoxin
 * @date Apr 9, 2018 11:30:26 AM
 */
public class Const {
	
	public static final String HOME_PATH = "/home/cmdFile";
	public static final String OPT_PATH = "/opt/SSLVPN";
	public static final String DATA_PATH = "/home/detectionData";
	

	/** ���з� */
	public static final String NEWLINE = System.getProperty("line.separator");

	/** tab������(4���ո�) */
	public static final String TAB = "\t";

	/** �����ʽ */
	public static final String UTF8 = "UTF-8";
	/** �����ļ�����·�� */
	public static final String CONFIG_PATH = HOME_PATH + "/NSTestTool/config" + File.separator;
	/** web�ļ�����·�� */
	public static final String WEB_PATH = HOME_PATH + "/NSTestTool/NSTTWeb" + File.separator;
	/** web�����ļ�����·�� */
	public static final String WEB_CONFIG_PATH = WEB_PATH + "config" + File.separator;
	/** tool�ļ�����·�� **/
	public static final String TOOL_PATH = HOME_PATH + "/NSTestTool" + File.separator;


	/** ������ݵ�Ŀ¼ */
	public static final String DETECTION_DATA_PATH = DATA_PATH + File.separator;

	/** ͳһ����session����û��� */
	public static final String LOGINNAME = "LoginName";
	
	/** �����ֲ�ĵ�ַ **/
	public static final String HELPBOOK = WEB_PATH + "help";
	/** ���Աʹ���ֲ� **/
	public static final String HELPBOOK_PDF = "help_book.pdf";
	
	/** SSLVPN��Ŀ¼ **/
	public static final String SSLVPN_ROOT_PATH = OPT_PATH + File.separator;
	
	/** ������־�ļ�Ŀ¼ **/
	public static final String TASK_LOG_PATH = SSLVPN_ROOT_PATH + "logs" + File.separator;
	
	/** ����Ա��־�ļ�Ŀ¼ **/
	public static final String ADMIN_LOG_PATH = WEB_PATH + "logs";

	/** SSLVPN��⹤����־Ŀ¼ **/
	public static final String SSLVPN_LOG_PATH = SSLVPN_ROOT_PATH + "logs" + File.separator;
	/** SSLVPN���ܿ��ű�Ŀ¼ **/
	public static final String CARD_SCRIPT_PATH = SSLVPN_ROOT_PATH + "run" + File.separator;
	/** ϵͳ���������ű�Ŀ¼ **/
	public static final String SYSTEM_SCRIPT_PATH = SSLVPN_ROOT_PATH + "system" + File.separator;
	/** �Ƿ���ʾ���ܿ���SM1�׼������ļ�Ŀ¼ **/
	public static final String CRYPTO_CARD_INI_PATH = SSLVPN_ROOT_PATH + "/crypto_card.ini";

	/** �û��Զ���Ӳ����Ϣ **/
	public static final String PRODUCTINFO = CONFIG_PATH + "product.properties";

	
	/** ��ҳ��ҳ�� **/
	public static final String HOME_PAGE = "/index.jsp";

	/** ��������Ŀ¼ **/
	public static final String CASE_LIB_PATH = Const.WEB_PATH + "caselib" + File.separator;

	/** �м����ݴ��·�� */
	public static final String INTERMEDIATE_DATA_PATH = Const.TOOL_PATH + "data" + File.separator;
	public static final String TEST_CASE_PATH = TOOL_PATH + "testCases" + File.separator;

	/** �û���Ϣxml�ļ� **/
	public static final String USER_CONFIG = WEB_CONFIG_PATH + "userconfig.xml";


	/** html�ļ�Ŀ¼ */
	public static final String DEFAULT_CONFIG = WEB_CONFIG_PATH + "default.properties";

	/**
	 * @Description ��Ӧ��Ӧ״̬��
	 * @author zhaoxin
	 * @date Apr 9, 2018 11:48:11 AM
	 * 
	 */
	public enum RespCode {

		/** ����ɹ� */
		SUCCESS(0, "SUCCESS"),

		/** ����ʧ�� */
		ERROR(-1, "ERROR");

		private int code;
		private String desc;

		RespCode(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return this.code;
		}

		public String getDesc() {
			return this.desc;
		}
	}
}
