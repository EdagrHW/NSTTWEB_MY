package cn.com.util;

import cn.com.exception.BizException;
import cn.com.interceptor.UserSession;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author WangKai
 * @ClassName: FileUtil
 * @date 2019-05-08 16:39
 * @Description:
 */
public class FileUtil {
	public static File getFile(File file, String fileName) {
		if (file != null) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null) {
					for (File f : files) {
						File found = getFile(f, fileName);
						if (found != null) {
							return found;
						}
					}
				}
			}
			return fileName.equalsIgnoreCase(file.getName()) ? file : null;
		}
		return null;
	}

	public static File getFile(String filePath, String fileName) {
		File file = getFile(new File(filePath), fileName);
		return getFile(file, fileName);
	}

	public static Boolean checkFileExist(String filePath, String fileName) {
		return getFile(new File(filePath), fileName) != null;
	}

	/**
	 * 指定目录下创建空文件夹
	 *
	 * @param filePath
	 * @param args
	 * @return
	 */
	public static Boolean createNewFileDir(String filePath, String... args) {
		for (String s : args) {
			String path = filePath;
			StringBuilder filePathBuilder = new StringBuilder(filePath);
			// 如果dir不以文件分隔符结尾，自动添加文件分隔符 防止跨平台出现路径问题
			if (!filePathBuilder.toString().endsWith(File.separator)) {
				filePathBuilder.append(File.separator).append(s);
			} else {
				filePathBuilder.append(s).append(File.separator);
			}

			path = filePathBuilder.toString();
			// 文件夹不存在则新建
			File fileDir = new File(path);
			if (!fileDir.exists()) {
				fileDir.setWritable(true);
				fileDir.mkdirs();
			}
		}
		return true;
	}

	/**
	 * 指定目录下创建空文件
	 *
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public static Boolean createNewFile(String filePath, String fileName) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符 防止跨平台出现路径问题
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		// 文件夹不存在则新建
		File fileDir = new File(filePath);
		if (!fileDir.exists()) {
			fileDir.setWritable(true);
			fileDir.mkdirs();
		}

		// 新建文件
		File myFile = new File(filePath, fileName);
		try {
			myFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 读文件
	 *
	 * @param filePath /Users/baonier/Downloads/
	 * @param fileName 2018.txt
	 * @return
	 */
	public static List<String> readerFile(String filePath, String fileName) {
		LogUtil.logInfo("call[FileUtils][readerFile]PARAMETER:[" + filePath + fileName + "]",
				UserSession.get("loginname"), null, null);
		long currentTime = System.currentTimeMillis();
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符 防止跨平台出现路径问题
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		// Text文件
		File file = new File(filePath + fileName);
		// 构造一个BufferedReader类来读取文件
		BufferedReader br = null;
		String s;
		String[] readers;
		List<String> readersList = new ArrayList<>();
		try {
			LogUtil.logInfo("开始读取文件路径：" + filePath + "中的：" + fileName, UserSession.get("loginname"), null, null);
			long readerTime = System.currentTimeMillis();
			br = new BufferedReader(new FileReader(file));
			// 使用readLine方法，一次读一行
			while ((s = br.readLine()) != null) {
				// 拆分
				readers = s.split("\\|");
				// 添加到集合中
				readersList.add(s + " separator");
			}
			LogUtil.logInfo(
					"读取文件路径：" + filePath + "中的：" + fileName + "一共耗时：" + (System.currentTimeMillis() - readerTime),
					UserSession.get("loginname"), null, null);
		} catch (IOException e) {
			LogUtil.logException("读取文件路径：" + filePath + "中的：" + fileName + "发生异常，异常信息是：", UserSession.get("loginname"),
					null, e);
			return null;
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				LogUtil.logException("关闭读取文件流发生异常，异常信息是：", UserSession.get("loginname"), null, e);
				return null;
			}
		}
		LogUtil.logInfo("call[FileUtils][readerFile]PARAMETER:[" + filePath + fileName,
				System.currentTimeMillis() - currentTime + "]", UserSession.get("loginname"), null);
		return readersList;
	}

	/**
	 * 读文件
	 *
	 * @param filePath /Users/baonier/Downloads/
	 * @param fileName 2018.txt
	 * @return
	 */
	public static String readerFileToString(String filePath, String fileName) {
		LogUtil.logInfo("call[FileUtils][readerFile]PARAMETER:[" + filePath + fileName + "]",
				UserSession.get("loginname"), null, null);
		long currentTime = System.currentTimeMillis();
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符 防止跨平台出现路径问题
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		// Text文件
		File file = new File(filePath + fileName);
		// 构造一个BufferedReader类来读取文件
		BufferedReader br = null;
		InputStreamReader read = null;
		String s;
		StringBuilder sb = new StringBuilder();
		try {
			LogUtil.logInfo("开始读取文件路径：" + filePath + "中的：" + fileName, UserSession.get("loginname"), null, null);
			long readerTime = System.currentTimeMillis();
			read = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
			br = new BufferedReader(read);
			// 使用readLine方法，一次读一行
			while ((s = br.readLine()) != null) {
				sb.append(s).append("\r\n");
			}
			LogUtil.logInfo(
					"读取文件路径：" + filePath + "中的：" + fileName + "一共耗时：" + (System.currentTimeMillis() - readerTime),
					UserSession.get("loginname"), null, null);
		} catch (IOException e) {
			LogUtil.logException("读取文件路径：" + filePath + "中的：" + fileName + "发生异常，异常信息是：", UserSession.get("loginname"),
					null, e);
			return null;
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (read != null) {
					read.close();
				}
			} catch (IOException e) {
				LogUtil.logException("关闭读取文件流发生异常，异常信息是：", UserSession.get("loginname"), null, e);
				// return null;
			}
		}
		LogUtil.logInfo("call[FileUtils][readerFile]PARAMETER:[" + filePath + fileName,
				System.currentTimeMillis() - currentTime + "]", UserSession.get("loginname"), null);
		return sb.toString();
	}

	/**
	 * 写文件
	 *
	 * @param readersList      数据
	 * @param filePath         路径 本地路径
	 * @param fileName         文件名 远程文件名称.txt
	 * @param controlWriterNum 从第几行开始写 例如： 从第2行开始 参数写 2
	 * @return
	 */
	public static Boolean writerFile(List<String[]> readersList, String filePath, String fileName,
			int controlWriterNum) {
		LogUtil.logInfo("call[FileUtils][writerFile]PARAMETER:[" + filePath + fileName + "]",
				UserSession.get("loginname"), null, null);
		long currentTime = System.currentTimeMillis();
		FileWriter fw = null;
		File file = new File(filePath + fileName);
		int writerNum = 0;
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符 防止跨平台出现路径问题
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		try {
			// 如果文件存在 那么删除它
			if (file.exists()) {
				file.getAbsolutePath();
				boolean isDelete = deleteFile(filePath + fileName);
				if (isDelete) {
					LogUtil.logInfo("文件已存在，原文件已删除", UserSession.get("loginname"), null, null);
				}
				file.createNewFile();
			}
			fw = new FileWriter(filePath + fileName);
			LogUtil.logInfo("开始写入文件路径：" + filePath + "中的：" + fileName, UserSession.get("loginname"), null, null);
			long writerTime = System.currentTimeMillis();
			for (String[] readers : readersList) {
				writerNum++;
				if (writerNum < controlWriterNum) {
					continue;
				}
				// 普通的写文件
				for (String reader : readers) {
					fw.write(reader);
				}

			}
			LogUtil.logInfo(
					"写入文件路径：" + filePath + "中的：" + fileName + "一共耗时：" + (System.currentTimeMillis() - writerTime),
					UserSession.get("loginname"), null, null);
		} catch (IOException e) {
			LogUtil.logFail("写入文件路径：" + filePath + "中的：" + fileName + "发生异常，异常信息是：", UserSession.get("loginname"), e);
			// 出现异常删除已经写的文件
			if (!file.exists()) {
				LogUtil.logFail("删除文件失败:" + fileName + "不存在！", UserSession.get("loginname"), null);
				return false;
			} else {
				// 如果是文件
				if (file.isFile()) {
					deleteFile(filePath + fileName);
				}
				// 如果是文件夹
				else {
					deleteDirectory(fileName);
				}
			}
			return false;
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				LogUtil.logException("关闭写入文件流发生异常，异常信息是：", UserSession.get("loginname"), null, e);
			}
		}
		LogUtil.logInfo("call[FileUtils][writerFile]PARAMETER:[" + filePath + fileName,
				System.currentTimeMillis() - currentTime + "]", UserSession.get("loginname"), null);
		return true;
	}

	/**
	 * 删除单个文件
	 *
	 * @param fileName 要删除的文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				LogUtil.logSuccess("删除单个文件" + fileName + "成功！", UserSession.get("loginname"), null);
				return true;
			} else {
				LogUtil.logFail("删除单个文件" + fileName + "失败！", UserSession.get("loginname"), null);
				return false;
			}
		} else {
			LogUtil.logFail("删除单个文件失败：" + fileName + "不存在！", UserSession.get("loginname"), null);
			return false;
		}
	}

	/**
	 * 删除目录及目录下的文件
	 *
	 * @param dir 要删除的目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符 防止跨平台出现路径问题
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，且不是一个目录，则退出
		if ((!dirFile.exists()) && (!dirFile.isDirectory())) {
			LogUtil.logFail("删除目录失败：" + dir + "不存在！", UserSession.get("loginname"), null);
			return false;
		}
		boolean flag = true;
		// 删除文件夹中的所有文件包括子目录
		File[] files = dirFile.listFiles();
		
		for (File file : files) {
			if(file.isFile()) {
				/*
				 * getAbsolutePath() 方法返回文件的绝对路径，如果构造的时候是全路径就直接返回全路径， 如果构造时是相对路径，就返回当前目录的路径 + 构造
				 * File 对象时的路径
				 */
				flag = deleteFile(file.getAbsolutePath());
				if (!flag) {
					break;
				}
			}
			// 删除子目录
			else if (file.isDirectory()) {
				flag = deleteDirectory(file.getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}
		if (!flag) {
			LogUtil.logFail("删除目录失败！", UserSession.get("loginname"), null);
			return false;
		}
		// 删除当前目录
		if (dirFile.delete()) {
			LogUtil.logSuccess("删除目录" + dir + "成功！", UserSession.get("loginname"), null);
			return true;
		} else {
			return false;
		}
	}

	public static byte[] fileTransToArrayByte(String path, String fileName) throws IOException {
		File file = new File(path, fileName);
		return fileTransToArrayByte(file);
	}

	public static byte[] fileTransToArrayByte(File file) throws IOException {
		try (InputStream inputStream = new FileInputStream(file);
				ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			byte[] by = new byte[1024];
			int len;
			while ((len = inputStream.read(by)) != -1) {
				out.write(by, 0, len);
			}
			byte[] data = out.toByteArray();
			inputStream.close();
			out.close();
			return data;
		} catch (IOException e) {
			throw e;
		}
	}

	public static void modifyToXML(File file, Document document) {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new FileOutputStream(file), OutputFormat.createPrettyPrint());
			writer.write(document);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BizException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void writeToXML(String path, String hoststr) {
		String[] hostarr = null;
		FileOutputStream fos = null;
		if (hoststr != null && !"".equals(hoststr)) {
			hostarr = hoststr.split("\r\n");
		}

		try {
			fos = new FileOutputStream(path);
			if (hostarr != null) {
				for (String s : hostarr) {
					fos.write((s + "\r\n").getBytes(StandardCharsets.UTF_8));
				}
			}
			fos.flush();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 生成zip工具类
	public static void getAllFileName(String path, ArrayList<String> listFileName) {
		File file = new File(path);
		File[] files = file.listFiles();
		String[] names = file.list();
		if (names != null) {
			String[] completNames = new String[names.length];
			for (int i = 0; i < names.length; i++) {
				completNames[i] = path + names[i];
			}
			listFileName.addAll(Arrays.asList(completNames));
		}
		for (File a : files) {
			if (a.isDirectory()) {// 如果文件夹下有子文件夹，获取子文件夹下的所有文件全路径。
				getAllFileName(a.getAbsolutePath() + "/", listFileName);
			}
		}
	}

	public static void compressedFile(String resourcesPath, String targetPath) throws Exception {
		File resourcesFile = new File(resourcesPath); // 源文件
		File targetFile = new File(targetPath); // 目的
		// 如果目的路径不存在，则新建
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		String targetName = resourcesFile.getName() + ".zip"; // 目的压缩文件名
		FileOutputStream outputStream = new FileOutputStream(targetPath + "/" + targetName);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(outputStream));
		createCompressedFile(out, resourcesFile, "");
		out.close();
	}

	/**
	 * @desc 生成压缩文件。 如果是文件夹，则使用递归，进行文件遍历、压缩 如果是文件，直接压缩
	 * @param out  输出流
	 * @param file 目标文件
	 * @return void
	 * @throws Exception
	 */
	public static void createCompressedFile(ZipOutputStream out, File file, String dir) throws Exception {
		// 如果当前的是文件夹，则进行进一步处理
		if (file.isDirectory()) {
			// 得到文件列表信息
			File[] files = file.listFiles();
			List<File> list = new ArrayList<>();
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isHidden() && !files[i].getName().endsWith(".bin")) {
					list.add(files[i]);
				}
			}
			File[] filterFile = new File[list.size()];
			// 将文件夹添加到下一级打包目录
			out.putNextEntry(new ZipEntry(dir + "/"));
			dir = dir.length() == 0 ? "" : dir + "/";
			// 循环将文件夹中的文件打包
			for (int i = 0; i < filterFile.length; i++) {
				createCompressedFile(out, list.get(i), dir + list.get(i).getName()); // 递归处理
			}
		} else { // 当前的是文件，打包处理
					// 文件输入流
			FileInputStream fis = new FileInputStream(file);
			out.putNextEntry(new ZipEntry(dir));
			// 进行写操作
			int j = 0;
			byte[] buffer = new byte[1024];
			while ((j = fis.read(buffer)) > 0) {
				out.write(buffer, 0, j);
			}
			// 关闭输入流
			fis.close();
		}
	}

	// 创建配置文件
	public static boolean createIniFile(String filePath, Map<String, Map<String, Object>> fileContent)
			throws IOException {
		File file = new File(filePath);
		Set<String> keys = null;
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));) {
			for (String section : fileContent.keySet()) {
				bufferedWriter.write("[" + section + "]" + "\n");
				keys = fileContent.get(section).keySet();
				for (String key : keys) {
					bufferedWriter.write(key + " = " + fileContent.get(section).get(key) + "\n");
				}
				bufferedWriter.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("文件写出异常");
		}
		return true;
	}

	// 读取配置文件
	public static Map<String, Map<String, String>> readIniFile(File file) throws IOException {
		// 存放所有的数据
		Map<String, Map<String, String>> dataMap = new LinkedHashMap<>();
		// 存放session下的所有value值
		Map<String, String> paramaters = null;

		try (BufferedReader br = new BufferedReader(new FileReader(file));) {
			// 读取的行
			String readLine = "";
			// 正则
			String regex = "\\[.+.\\]";
			// 存放读取的session
			String session = "";
			// 存放key和value值
			String[] map = null;
			while ((readLine = br.readLine()) != null) {
				if (null != readLine && !"".equals(readLine)) {
					if (readLine.matches(regex)) {
						if (null != session && !"".equals(session)) {
							dataMap.put(session, paramaters);
						}
						paramaters = new LinkedHashMap<>();
						session = readLine.substring(1, readLine.length() - 1);
					} else {
						map = readLine.split(" = ");
						paramaters.put(map[0], map.length > 1 ? map[1] : "");
					}
				}
			}
			dataMap.put(session, paramaters);
			return dataMap;
		}

	}

	// 读取不带session标识的 配置文件
	public static Map<String, String> readIniFileWithoutSession(File file) throws IOException {
		// 存放所有的数据
		Map<String, String> paramaters = new LinkedHashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader(file));) {
			// 读取的行
			String readLine = "";
			// 存放key和value值
			String[] map = null;
			while ((readLine = br.readLine()) != null) {
				if (null != readLine && !"".equals(readLine)) {
					map = readLine.split(" = ");
					paramaters.put(map[0], map.length > 1 ? map[1] : "");
				}
			}
			return paramaters;
		}

	}

	public static void formatFileName(String browser, String fileName, HttpServletResponse response) {
		try {
			if (-1 < browser.indexOf("MSIE 6.0") || -1 < browser.indexOf("MSIE 7.0")) {
				// IE6, IE7 浏览器
				response.addHeader("content-disposition",
						"attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
			} else if (-1 < browser.indexOf("MSIE 8.0")) {
				// IE8
				response.addHeader("content-disposition",
						"attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			} else if (-1 < browser.indexOf("MSIE 9.0")) {
				// IE9
				response.addHeader("content-disposition",
						"attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			} else if (-1 < browser.indexOf("Chrome")) {
				// 谷歌
				response.addHeader("content-disposition",
						"attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
			} else if (-1 < browser.indexOf("Safari")) {
				// 苹果
				response.addHeader("content-disposition",
						"attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
			} else {
				// 火狐或者其他的浏览器
				response.addHeader("content-disposition",
						"attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 复制单个文件*
	 * 
	 * @param oldPath String 原文件路径 如：c:/fqf.txt*
	 * @param newPath String 复制后路径 如：f:/fqf.txt*
	 * @return boolean
	 */

	public static boolean copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath String 原文件路径 如：c:/fqf
	 * @param newPath String 复制后路径 如：f:/ff
	 * @return boolean
	 */
	public static boolean copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			File[] file = a.listFiles();
			for (int i = 0; i < file.length; i++) {
				if (file[i].isFile()) {
					copyFile(oldPath + File.separator + file[i].getName(), newPath + File.separator + file[i].getName());
				}else if (file[i].isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + File.separator + file[i].getName(), newPath + File.separator + file[i].getName());
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
