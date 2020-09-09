package cn.com.common;

import java.io.File;

/**
 * 常量类
 * 
 * @author zhaoxin
 * @date Apr 9, 2018 11:30:26 AM
 */
public class Const {
	
	public static final String HOME_PATH = "/home/cmdFile";
	public static final String OPT_PATH = "/opt/SSLVPN";
	public static final String DATA_PATH = "/home/detectionData";
	

	/** 换行符 */
	public static final String NEWLINE = System.getProperty("line.separator");

	/** tab键距离(4个空格) */
	public static final String TAB = "\t";

	/** 编码格式 */
	public static final String UTF8 = "UTF-8";
	/** 配置文件管理路径 */
	public static final String CONFIG_PATH = HOME_PATH + "/NSTestTool/config" + File.separator;
	/** web文件管理路径 */
	public static final String WEB_PATH = HOME_PATH + "/NSTestTool/NSTTWeb" + File.separator;
	/** web配置文件管理路径 */
	public static final String WEB_CONFIG_PATH = WEB_PATH + "config" + File.separator;
	/** tool文件管理路径 **/
	public static final String TOOL_PATH = HOME_PATH + "/NSTestTool" + File.separator;


	/** 检测数据的目录 */
	public static final String DETECTION_DATA_PATH = DATA_PATH + File.separator;

	/** 统一存在session里的用户键 */
	public static final String LOGINNAME = "LoginName";
	
	/** 帮助手册的地址 **/
	public static final String HELPBOOK = WEB_PATH + "help";
	/** 审计员使用手册 **/
	public static final String HELPBOOK_PDF = "help_book.pdf";
	
	/** SSLVPN根目录 **/
	public static final String SSLVPN_ROOT_PATH = OPT_PATH + File.separator;
	
	/** 任务日志文件目录 **/
	public static final String TASK_LOG_PATH = SSLVPN_ROOT_PATH + "logs" + File.separator;
	
	/** 管理员日志文件目录 **/
	public static final String ADMIN_LOG_PATH = WEB_PATH + "logs";

	/** SSLVPN检测工具日志目录 **/
	public static final String SSLVPN_LOG_PATH = SSLVPN_ROOT_PATH + "logs" + File.separator;
	/** SSLVPN加密卡脚本目录 **/
	public static final String CARD_SCRIPT_PATH = SSLVPN_ROOT_PATH + "run" + File.separator;
	/** 系统升级重启脚本目录 **/
	public static final String SYSTEM_SCRIPT_PATH = SSLVPN_ROOT_PATH + "system" + File.separator;
	/** 是否显示加密卡、SM1套件配置文件目录 **/
	public static final String CRYPTO_CARD_INI_PATH = SSLVPN_ROOT_PATH + "/crypto_card.ini";

	/** 用户自定义硬件信息 **/
	public static final String PRODUCTINFO = CONFIG_PATH + "product.properties";

	
	/** 首页主页面 **/
	public static final String HOME_PAGE = "/index.jsp";

	/** 测试用例目录 **/
	public static final String CASE_LIB_PATH = Const.WEB_PATH + "caselib" + File.separator;

	/** 中间数据存放路径 */
	public static final String INTERMEDIATE_DATA_PATH = Const.TOOL_PATH + "data" + File.separator;
	public static final String TEST_CASE_PATH = TOOL_PATH + "testCases" + File.separator;

	/** 用户信息xml文件 **/
	public static final String USER_CONFIG = WEB_CONFIG_PATH + "userconfig.xml";


	/** html文件目录 */
	public static final String DEFAULT_CONFIG = WEB_CONFIG_PATH + "default.properties";

	/**
	 * @Description 响应对应状态码
	 * @author zhaoxin
	 * @date Apr 9, 2018 11:48:11 AM
	 * 
	 */
	public enum RespCode {

		/** 请求成功 */
		SUCCESS(0, "SUCCESS"),

		/** 请求失败 */
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
