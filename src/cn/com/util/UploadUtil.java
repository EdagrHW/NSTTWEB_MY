package cn.com.util;

import cn.com.exception.BizException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WangKai
 * @ClassName: UploadUtil
 * @date 2019-08-20 14:34
 * @Description:
 */
public class UploadUtil {
    /**
     * 批量上传文件
     *
     * @param map
     * @return
     */
    public static boolean batchUpload(Map<String, Object> map) {
        boolean flag;
        try {
            for (String path : map.keySet()) {
                MultipartFile uploadFile = (MultipartFile) map.get(path);
                //构建文件对象
                File file = new File(path);
                if (file.exists() || file.isDirectory()) {
                    file.delete();
                }
                if (!uploadFile.isEmpty()) {
                    boolean mkdirs = file.mkdirs();
                    if (mkdirs) {
                        Map<String, Object> exec;
                        uploadFile.transferTo(new File(path + File.separator + uploadFile.getOriginalFilename()));
                        if (uploadFile.getOriginalFilename().endsWith(".tgz")) {
                            //String shell="tar -zxvf " + path + File.separator + uploadFile.getOriginalFilename()+" --strip-components 1 -C "+path + File.separator;
                            String shell = "tar -zxvf " + path + File.separator + uploadFile.getOriginalFilename() + " -C " + path + File.separator;
                             ShellUtil.exec(shell);
                            String tgzPath=path + File.separator + uploadFile.getOriginalFilename();
                            //tgzPath:/home/cmdFile/NSTestTool/destlib/CCTC-AK-2019-0067/CCTC-AK-2019-0014
                            tgzPath=tgzPath.substring(0,tgzPath.lastIndexOf(".tgz"));
                            File tgzFile = new File(tgzPath);
                            if (tgzFile.exists()&&tgzFile.isDirectory()) {
                                StringBuilder files = new StringBuilder();
                                moveFile(files, tgzFile);
                                if(files.length()>0) {
                                    ShellUtil.exec("mv -f " + files.toString() + " " + path);
                                  ShellUtil.exec("rm -rf " + tgzPath);
                                }
                           }
                            exec=ShellUtil.exec("rm -rf " + path + File.separator + uploadFile.getOriginalFilename());
                            if (!(Boolean) exec.get("success")) {
                                ShellUtil.exec("rm -rf " + path + File.separator);
                                return false;
                            }
                        }
                    }
                }
            }
            flag = true;
        } catch (Exception e) {
            // flag=false;
            e.printStackTrace();
            throw new BizException("upload libs is failed");
        }
        return flag;

    }

    public static void moveFile(StringBuilder fils, File file) {
        try {
            for (File fi : file.listFiles()) {
                if (fi.isDirectory()) {
                    moveFile(fils, fi);
                } else {
                    fils.append(fi.getAbsolutePath()).append(" ");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Map<String, Object> untarTgz(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        try {
            for (String path : map.keySet()) {
                String fileName = (String) map.get(path);
                result = ShellUtil.exec("tar -zxvf " + path + File.separator + fileName);
                ShellUtil.exec("rm -rf " + path + File.separator + fileName);
            }
        } catch (Exception e) {
            throw new BizException("upload libs is failed");
        }
        return result;
    }
}
