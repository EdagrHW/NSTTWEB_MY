package cn.com.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaoxin
 * @description 执行脚本工具
 * @date 2019/8/13 16:17
 */
public class ShellUtil {

    /**
     * @param cmds
     * @return Map<String, Object>
     * key----->value
     * success  true/false
     * msg----->返回结果
     * exception--->异常对象
     * @description 执行命令
     * 命令带参数时参数可作为其中一个参数
     * 也可以将命令和参数组合为一个字符串传入
     * @author zhaoxin
     * @date 2019/8/13 16:30
     */
    public static Map<String, Object> exec(String... cmds) {
        HashMap<String, Object> map = new HashMap<>(8);
        try {
            Process process = RuntimeUtil.exec(cmds);
            return getResult(process, map);
        } catch (Exception ex) {
            ex.printStackTrace();
            map.put("success", false);
            map.put("msg", ex.getMessage());
            map.put("exception", ex);
        }
        return map;
    }


    /**
     * @param cmds
     * @return Map<String, Object>
     * key----->value
     * success  true/false
     * msg----->返回结果
     * exception--->异常对象
     * @description 同时执行多条命令
     * 命令带参数时参数可作为其中一个参数
     * @author zhaoxin
     * @date 2019/8/13 16:30
     */
    public static Map<String, Object> batchExec(String... cmds) {
        HashMap<String, Object> map = new HashMap<>(8);
        DataOutputStream os=null;
        try {
            Runtime runtime = Runtime.getRuntime();
            String[] addr={"LANG=zh_CN.UTF-8","LC_ALL=zh_CN.UTF-8"};
            Process process = runtime.exec("sh",addr);
            OutputStream outputStream = process.getOutputStream();
            os = new DataOutputStream(outputStream);
            for(String cmd:cmds){
                os.writeBytes(cmd + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();
            IoUtil.close(os);
            return getResult2(process, map);
        } catch (Exception ex) {
            ex.printStackTrace();
            map.put("success", false);
            map.put("msg", ex.getMessage());
            map.put("exception", ex);
        }finally {
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    /**
     * @param dir  执行命令所在目录，null表示使用当前进程执行的目录
     * @param cmds 命令（命令带参数时参数可作为其中一个参数，
     *             也可以将命令和参数组合为一个字符串传入）
     * @return Map<String, Object>
     * key----->value
     * success  true/false
     * msg----->返回结果
     * exception--->异常对象
     * @description 执行shell脚本
     * @author zhaoxin
     * @date 2019/8/13 16:53
     */
    public static Map<String, Object> exec(File dir, String... cmds) {
        HashMap<String, Object> map = new HashMap<>(8);
        try {
            Process process = RuntimeUtil.exec(null, dir, cmds);
            return getResult(process, map);
        } catch (Exception ex) {
            map.put("success", false);
            map.put("msg", ex.getMessage());
            map.put("exception", ex);
        }
        return map;

    }

    public static Map<String, Object> getResult2(Process process, Map<String, Object> map) {
        // 异常输入流
        BufferedReader error = null;
        // 正确输入流
        BufferedReader input = null;
        try {
            // 换行符
            String line = System.getProperty("line.separator");
            boolean waitFor = process.waitFor(10, TimeUnit.SECONDS);
            if (!waitFor) {
                StringBuilder buf = new StringBuilder();
                error = new BufferedReader(new InputStreamReader(
                        process.getErrorStream()));
                String content;
                while ((content = error.readLine()) != null) {
                    buf.append(content).append(line);
                }
                map.put("success", false);
                map.put("msg", buf.toString());
            } else {
                StringBuilder buf = new StringBuilder();
                input = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                String content;
                while ((content = input.readLine()) != null) {
                    buf.append(content).append(line);
                }
                map.put("success", true);
                map.put("msg", buf.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            map.put("success", false);
            map.put("msg", ex.getMessage());
            map.put("exception", ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (error != null) {
                try {
                    error.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return map;
    }

    public static Map<String, Object> getResult(Process process, Map<String, Object> map) {
        try {
            if (process.waitFor() != 0) {

                StringBuilder buf = new StringBuilder();
                String result = getResult(process);
                if (StrUtil.isNotBlank(result)) {
                    buf.append(result);
                }
                String error = RuntimeUtil.getErrorResult(process);
                if (StrUtil.isNotBlank(error)) {
                    buf.append(error);
                }
                map.put("success", false);
                map.put("msg", buf.toString());
            } else {
                StringBuilder buf = new StringBuilder();
                String result = RuntimeUtil.getResult(process);
                if (StrUtil.isNotBlank(result)) {
                    buf.append(result);
                }
                map.put("success", true);
                map.put("msg", buf.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            map.put("success", false);
            map.put("msg", ex.getMessage());
            map.put("exception", ex);
        }
        return map;
    }

    public static Map<String, Object> backOrRoll(String path, String fileName, String type) {
        Map<String, Object> map = new HashMap<>(8);
        String backPath = "/tmp" + File.separator;
        String filPath = path;
        if ("back".equals(type)) {
            if (fileName != null) {
                filPath = filPath + File.separator + fileName;
            } else {
                backPath = backPath + filPath.substring(filPath.lastIndexOf(File.separator) + 1);
                batchExec("mkdir -p " + backPath);
                filPath = filPath + File.separator + "*";
            }
            map = batchExec("\\cp -f " + filPath + " " + backPath);
        } else if ("roll".equals(type)) {
            if (fileName != null) {
                backPath = backPath + File.separator + fileName;
            } else {
                backPath = backPath + filPath.substring(filPath.lastIndexOf(File.separator) + 1);
                backPath = backPath + File.separator + "*";
            }
            File file = new File(backPath);
            if (file.exists()) {
                boolean isNo= Optional.ofNullable(file.listFiles()).map(lib->lib.length >0).orElse(false);
                boolean flag = file.isDirectory() && isNo;
                if (flag || file.isFile()) {
                    map = batchExec("\\cp -f " + backPath + " " + path);
                }
            }
        } else if ("delback".equals(type)) {
            if (fileName != null) {
                backPath = backPath + fileName;
            } else {
                backPath = backPath + filPath.substring(filPath.lastIndexOf(File.separator) + 1);
            }
            map = batchExec("rm -rf " + backPath);
        }

        return map;
    }

    public static String getResult(Process process) {
        InputStream in = process.getInputStream();
        return IoUtil.read(in, CharsetUtil.systemCharset());

    }



}

