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
	 * ָ��Ŀ¼�´������ļ���
	 *
	 * @param filePath
	 * @param args
	 * @return
	 */
	public static Boolean createNewFileDir(String filePath, String... args) {
		for (String s : args) {
			String path = filePath;
			StringBuilder filePathBuilder = new StringBuilder(filePath);
			// ���dir�����ļ��ָ�����β���Զ�����ļ��ָ��� ��ֹ��ƽ̨����·������
			if (!filePathBuilder.toString().endsWith(File.separator)) {
				filePathBuilder.append(File.separator).append(s);
			} else {
				filePathBuilder.append(s).append(File.separator);
			}

			path = filePathBuilder.toString();
			// �ļ��в��������½�
			File fileDir = new File(path);
			if (!fileDir.exists()) {
				fileDir.setWritable(true);
				fileDir.mkdirs();
			}
		}
		return true;
	}

	/**
	 * ָ��Ŀ¼�´������ļ�
	 *
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public static Boolean createNewFile(String filePath, String fileName) {
		// ���dir�����ļ��ָ�����β���Զ�����ļ��ָ��� ��ֹ��ƽ̨����·������
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		// �ļ��в��������½�
		File fileDir = new File(filePath);
		if (!fileDir.exists()) {
			fileDir.setWritable(true);
			fileDir.mkdirs();
		}

		// �½��ļ�
		File myFile = new File(filePath, fileName);
		try {
			myFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * ���ļ�
	 *
	 * @param filePath /Users/baonier/Downloads/
	 * @param fileName 2018.txt
	 * @return
	 */
	public static List<String> readerFile(String filePath, String fileName) {
		LogUtil.logInfo("call[FileUtils][readerFile]PARAMETER:[" + filePath + fileName + "]",
				UserSession.get("loginname"), null, null);
		long currentTime = System.currentTimeMillis();
		// ���dir�����ļ��ָ�����β���Զ�����ļ��ָ��� ��ֹ��ƽ̨����·������
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		// Text�ļ�
		File file = new File(filePath + fileName);
		// ����һ��BufferedReader������ȡ�ļ�
		BufferedReader br = null;
		String s;
		String[] readers;
		List<String> readersList = new ArrayList<>();
		try {
			LogUtil.logInfo("��ʼ��ȡ�ļ�·����" + filePath + "�еģ�" + fileName, UserSession.get("loginname"), null, null);
			long readerTime = System.currentTimeMillis();
			br = new BufferedReader(new FileReader(file));
			// ʹ��readLine������һ�ζ�һ��
			while ((s = br.readLine()) != null) {
				// ���
				readers = s.split("\\|");
				// ��ӵ�������
				readersList.add(s + " separator");
			}
			LogUtil.logInfo(
					"��ȡ�ļ�·����" + filePath + "�еģ�" + fileName + "һ����ʱ��" + (System.currentTimeMillis() - readerTime),
					UserSession.get("loginname"), null, null);
		} catch (IOException e) {
			LogUtil.logException("��ȡ�ļ�·����" + filePath + "�еģ�" + fileName + "�����쳣���쳣��Ϣ�ǣ�", UserSession.get("loginname"),
					null, e);
			return null;
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				LogUtil.logException("�رն�ȡ�ļ��������쳣���쳣��Ϣ�ǣ�", UserSession.get("loginname"), null, e);
				return null;
			}
		}
		LogUtil.logInfo("call[FileUtils][readerFile]PARAMETER:[" + filePath + fileName,
				System.currentTimeMillis() - currentTime + "]", UserSession.get("loginname"), null);
		return readersList;
	}

	/**
	 * ���ļ�
	 *
	 * @param filePath /Users/baonier/Downloads/
	 * @param fileName 2018.txt
	 * @return
	 */
	public static String readerFileToString(String filePath, String fileName) {
		LogUtil.logInfo("call[FileUtils][readerFile]PARAMETER:[" + filePath + fileName + "]",
				UserSession.get("loginname"), null, null);
		long currentTime = System.currentTimeMillis();
		// ���dir�����ļ��ָ�����β���Զ�����ļ��ָ��� ��ֹ��ƽ̨����·������
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		// Text�ļ�
		File file = new File(filePath + fileName);
		// ����һ��BufferedReader������ȡ�ļ�
		BufferedReader br = null;
		InputStreamReader read = null;
		String s;
		StringBuilder sb = new StringBuilder();
		try {
			LogUtil.logInfo("��ʼ��ȡ�ļ�·����" + filePath + "�еģ�" + fileName, UserSession.get("loginname"), null, null);
			long readerTime = System.currentTimeMillis();
			read = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
			br = new BufferedReader(read);
			// ʹ��readLine������һ�ζ�һ��
			while ((s = br.readLine()) != null) {
				sb.append(s).append("\r\n");
			}
			LogUtil.logInfo(
					"��ȡ�ļ�·����" + filePath + "�еģ�" + fileName + "һ����ʱ��" + (System.currentTimeMillis() - readerTime),
					UserSession.get("loginname"), null, null);
		} catch (IOException e) {
			LogUtil.logException("��ȡ�ļ�·����" + filePath + "�еģ�" + fileName + "�����쳣���쳣��Ϣ�ǣ�", UserSession.get("loginname"),
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
				LogUtil.logException("�رն�ȡ�ļ��������쳣���쳣��Ϣ�ǣ�", UserSession.get("loginname"), null, e);
				// return null;
			}
		}
		LogUtil.logInfo("call[FileUtils][readerFile]PARAMETER:[" + filePath + fileName,
				System.currentTimeMillis() - currentTime + "]", UserSession.get("loginname"), null);
		return sb.toString();
	}

	/**
	 * д�ļ�
	 *
	 * @param readersList      ����
	 * @param filePath         ·�� ����·��
	 * @param fileName         �ļ��� Զ���ļ�����.txt
	 * @param controlWriterNum �ӵڼ��п�ʼд ���磺 �ӵ�2�п�ʼ ����д 2
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
		// ���dir�����ļ��ָ�����β���Զ�����ļ��ָ��� ��ֹ��ƽ̨����·������
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		try {
			// ����ļ����� ��ôɾ����
			if (file.exists()) {
				file.getAbsolutePath();
				boolean isDelete = deleteFile(filePath + fileName);
				if (isDelete) {
					LogUtil.logInfo("�ļ��Ѵ��ڣ�ԭ�ļ���ɾ��", UserSession.get("loginname"), null, null);
				}
				file.createNewFile();
			}
			fw = new FileWriter(filePath + fileName);
			LogUtil.logInfo("��ʼд���ļ�·����" + filePath + "�еģ�" + fileName, UserSession.get("loginname"), null, null);
			long writerTime = System.currentTimeMillis();
			for (String[] readers : readersList) {
				writerNum++;
				if (writerNum < controlWriterNum) {
					continue;
				}
				// ��ͨ��д�ļ�
				for (String reader : readers) {
					fw.write(reader);
				}

			}
			LogUtil.logInfo(
					"д���ļ�·����" + filePath + "�еģ�" + fileName + "һ����ʱ��" + (System.currentTimeMillis() - writerTime),
					UserSession.get("loginname"), null, null);
		} catch (IOException e) {
			LogUtil.logFail("д���ļ�·����" + filePath + "�еģ�" + fileName + "�����쳣���쳣��Ϣ�ǣ�", UserSession.get("loginname"), e);
			// �����쳣ɾ���Ѿ�д���ļ�
			if (!file.exists()) {
				LogUtil.logFail("ɾ���ļ�ʧ��:" + fileName + "�����ڣ�", UserSession.get("loginname"), null);
				return false;
			} else {
				// ������ļ�
				if (file.isFile()) {
					deleteFile(filePath + fileName);
				}
				// ������ļ���
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
				LogUtil.logException("�ر�д���ļ��������쳣���쳣��Ϣ�ǣ�", UserSession.get("loginname"), null, e);
			}
		}
		LogUtil.logInfo("call[FileUtils][writerFile]PARAMETER:[" + filePath + fileName,
				System.currentTimeMillis() - currentTime + "]", UserSession.get("loginname"), null);
		return true;
	}

	/**
	 * ɾ�������ļ�
	 *
	 * @param fileName Ҫɾ�����ļ����ļ���
	 * @return �����ļ�ɾ���ɹ�����true�����򷵻�false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// ����ļ�·������Ӧ���ļ����ڣ�������һ���ļ�����ֱ��ɾ��
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				LogUtil.logSuccess("ɾ�������ļ�" + fileName + "�ɹ���", UserSession.get("loginname"), null);
				return true;
			} else {
				LogUtil.logFail("ɾ�������ļ�" + fileName + "ʧ�ܣ�", UserSession.get("loginname"), null);
				return false;
			}
		} else {
			LogUtil.logFail("ɾ�������ļ�ʧ�ܣ�" + fileName + "�����ڣ�", UserSession.get("loginname"), null);
			return false;
		}
	}

	/**
	 * ɾ��Ŀ¼��Ŀ¼�µ��ļ�
	 *
	 * @param dir Ҫɾ����Ŀ¼���ļ�·��
	 * @return Ŀ¼ɾ���ɹ�����true�����򷵻�false
	 */
	public static boolean deleteDirectory(String dir) {
		// ���dir�����ļ��ָ�����β���Զ�����ļ��ָ��� ��ֹ��ƽ̨����·������
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		
		File dirFile = new File(dir);
		// ���dir��Ӧ���ļ������ڣ��Ҳ���һ��Ŀ¼�����˳�
		if ((!dirFile.exists()) && (!dirFile.isDirectory())) {
			LogUtil.logFail("ɾ��Ŀ¼ʧ�ܣ�" + dir + "�����ڣ�", UserSession.get("loginname"), null);
			return false;
		}
		boolean flag = true;
		// ɾ���ļ����е������ļ�������Ŀ¼
		File[] files = dirFile.listFiles();
		
		for (File file : files) {
			if(file.isFile()) {
				/*
				 * getAbsolutePath() ���������ļ��ľ���·������������ʱ����ȫ·����ֱ�ӷ���ȫ·���� �������ʱ�����·�����ͷ��ص�ǰĿ¼��·�� + ����
				 * File ����ʱ��·��
				 */
				flag = deleteFile(file.getAbsolutePath());
				if (!flag) {
					break;
				}
			}
			// ɾ����Ŀ¼
			else if (file.isDirectory()) {
				flag = deleteDirectory(file.getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}
		if (!flag) {
			LogUtil.logFail("ɾ��Ŀ¼ʧ�ܣ�", UserSession.get("loginname"), null);
			return false;
		}
		// ɾ����ǰĿ¼
		if (dirFile.delete()) {
			LogUtil.logSuccess("ɾ��Ŀ¼" + dir + "�ɹ���", UserSession.get("loginname"), null);
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

	// ����zip������
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
			if (a.isDirectory()) {// ����ļ����������ļ��У���ȡ���ļ����µ������ļ�ȫ·����
				getAllFileName(a.getAbsolutePath() + "/", listFileName);
			}
		}
	}

	public static void compressedFile(String resourcesPath, String targetPath) throws Exception {
		File resourcesFile = new File(resourcesPath); // Դ�ļ�
		File targetFile = new File(targetPath); // Ŀ��
		// ���Ŀ��·�������ڣ����½�
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		String targetName = resourcesFile.getName() + ".zip"; // Ŀ��ѹ���ļ���
		FileOutputStream outputStream = new FileOutputStream(targetPath + "/" + targetName);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(outputStream));
		createCompressedFile(out, resourcesFile, "");
		out.close();
	}

	/**
	 * @desc ����ѹ���ļ��� ������ļ��У���ʹ�õݹ飬�����ļ�������ѹ�� ������ļ���ֱ��ѹ��
	 * @param out  �����
	 * @param file Ŀ���ļ�
	 * @return void
	 * @throws Exception
	 */
	public static void createCompressedFile(ZipOutputStream out, File file, String dir) throws Exception {
		// �����ǰ�����ļ��У�����н�һ������
		if (file.isDirectory()) {
			// �õ��ļ��б���Ϣ
			File[] files = file.listFiles();
			List<File> list = new ArrayList<>();
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isHidden() && !files[i].getName().endsWith(".bin")) {
					list.add(files[i]);
				}
			}
			File[] filterFile = new File[list.size()];
			// ���ļ�����ӵ���һ�����Ŀ¼
			out.putNextEntry(new ZipEntry(dir + "/"));
			dir = dir.length() == 0 ? "" : dir + "/";
			// ѭ�����ļ����е��ļ����
			for (int i = 0; i < filterFile.length; i++) {
				createCompressedFile(out, list.get(i), dir + list.get(i).getName()); // �ݹ鴦��
			}
		} else { // ��ǰ�����ļ����������
					// �ļ�������
			FileInputStream fis = new FileInputStream(file);
			out.putNextEntry(new ZipEntry(dir));
			// ����д����
			int j = 0;
			byte[] buffer = new byte[1024];
			while ((j = fis.read(buffer)) > 0) {
				out.write(buffer, 0, j);
			}
			// �ر�������
			fis.close();
		}
	}

	// ���������ļ�
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
			throw new IOException("�ļ�д���쳣");
		}
		return true;
	}

	// ��ȡ�����ļ�
	public static Map<String, Map<String, String>> readIniFile(File file) throws IOException {
		// ������е�����
		Map<String, Map<String, String>> dataMap = new LinkedHashMap<>();
		// ���session�µ�����valueֵ
		Map<String, String> paramaters = null;

		try (BufferedReader br = new BufferedReader(new FileReader(file));) {
			// ��ȡ����
			String readLine = "";
			// ����
			String regex = "\\[.+.\\]";
			// ��Ŷ�ȡ��session
			String session = "";
			// ���key��valueֵ
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

	// ��ȡ����session��ʶ�� �����ļ�
	public static Map<String, String> readIniFileWithoutSession(File file) throws IOException {
		// ������е�����
		Map<String, String> paramaters = new LinkedHashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader(file));) {
			// ��ȡ����
			String readLine = "";
			// ���key��valueֵ
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
				// IE6, IE7 �����
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
				// �ȸ�
				response.addHeader("content-disposition",
						"attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
			} else if (-1 < browser.indexOf("Safari")) {
				// ƻ��
				response.addHeader("content-disposition",
						"attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
			} else {
				// ������������������
				response.addHeader("content-disposition",
						"attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * ���Ƶ����ļ�*
	 * 
	 * @param oldPath String ԭ�ļ�·�� �磺c:/fqf.txt*
	 * @param newPath String ���ƺ�·�� �磺f:/fqf.txt*
	 * @return boolean
	 */

	public static boolean copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // �ļ�����ʱ
				InputStream inStream = new FileInputStream(oldPath); // ����ԭ�ļ�
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // �ֽ��� �ļ���С
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("���Ƶ����ļ���������");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * ���������ļ�������
	 * 
	 * @param oldPath String ԭ�ļ�·�� �磺c:/fqf
	 * @param newPath String ���ƺ�·�� �磺f:/ff
	 * @return boolean
	 */
	public static boolean copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // ����ļ��в����� �������ļ���
			File a = new File(oldPath);
			File[] file = a.listFiles();
			for (int i = 0; i < file.length; i++) {
				if (file[i].isFile()) {
					copyFile(oldPath + File.separator + file[i].getName(), newPath + File.separator + file[i].getName());
				}else if (file[i].isDirectory()) {// ��������ļ���
					copyFolder(oldPath + File.separator + file[i].getName(), newPath + File.separator + file[i].getName());
				}
			}
		} catch (Exception e) {
			System.out.println("���������ļ������ݲ�������");
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
