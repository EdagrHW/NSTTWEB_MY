package cn.com.entity.dto;

import cn.com.entity.UseCaseLibrary;
import cn.com.util.Dom4jUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author WangKai
 * @ClassName: UseCaseLibraryDto
 * @date 2019-08-19 10:28
 * @Description:
 */
public class UseCaseLibraryDto {
    private UseCaseLibrary useCaseLibrary;
    private String referenceNum;
    private List<String> referenceTasks=new ArrayList<>();



    /**
     * @return Document
     * @description 实体类转化配置文件
     * @author wangkai
     * @date 2019/8/15 14:15
     */
    public void toXml(Element root){
        creatXML(root);
    }

    public Document toXml(File file) {

        Document document;
        Element root;
        try {
            document = Dom4jUtil.readXML(file);
            root = document.getRootElement();
            String format= String.format("//*[@casename=\"%s\"]",useCaseLibrary.getCaseName());
            List<Node> nodes = root.selectNodes(format);
            for(Node node:nodes){
                root.remove(node);
            }
        } catch (Exception ignore) {
            document = Dom4jUtil.createXml();
            root = Dom4jUtil.addChild(document, "cases");
        }
        creatXML(root);
        return document;
    }

    private void creatXML(Element root){
        Element child;
        child = Dom4jUtil.addChild(root, "casesnum");
        child.addAttribute("casename",useCaseLibrary.getCaseName());
        child.addComment("用例库名称");
        Element caseNameElement = Dom4jUtil.addChild(child, "casename");
        Dom4jUtil.addText(caseNameElement, useCaseLibrary.getCaseName(), StrUtil.EMPTY);
        child.addComment("用例库名称UUID标识");
        Element caseNameUUIDElement = Dom4jUtil.addChild(child, "casenameuuid");
        Dom4jUtil.addText(caseNameUUIDElement, useCaseLibrary.getCaseNameUUID(), StrUtil.EMPTY);
        child.addComment("待测设备类型");
        Element deviceTypeElement = Dom4jUtil.addChild(child, "devicetype");
        String deviceType = useCaseLibrary.getDeviceType();
        Dom4jUtil.addText(deviceTypeElement, deviceType, "server");
        child.addComment("模式");
        Element modeElement = Dom4jUtil.addChild(child, "mode");
        String mode = useCaseLibrary.getMode();
        Dom4jUtil.addText(modeElement, mode, "CSMODE");
        child.addComment("用例选择");
        Element casesElement = Dom4jUtil.addChild(child, "caseslist");
        Dom4jUtil.addText(casesElement, useCaseLibrary.getCasesName().toString(), StrUtil.EMPTY);
        child.addComment("引用状态");
        Element referenceNumElement = Dom4jUtil.addChild(child, "reference");
        Dom4jUtil.addText(referenceNumElement, referenceNum, "0");
        Element referenceTaskElement = Dom4jUtil.addChild(child, "referencetask");
        if(referenceTasks.isEmpty()) {
        	Dom4jUtil.addText(referenceTaskElement, StrUtil.EMPTY, StrUtil.EMPTY);
        }else {
        	Dom4jUtil.addText(referenceTaskElement, referenceTasks.toString(), StrUtil.EMPTY);
        }
        //DateUtil.format(lastDate,"yyyy-MM-dd hh:mm:ss")
        child.addComment("用例创建时间（yyyy-MM-dd HH:mm:ss）");
        Element lastNode = Dom4jUtil.addChild(child, "lastdate");
        lastNode.addText(DateUtil.format(useCaseLibrary.getTime(), "yyyy-MM-dd HH:mm:ss"));
    }
    /**
     * @description
     *     解析配置文件
     * @param map
     * @return
     * @author Wangkai
     * @date 2019/8/12 14:34
     */
    public void parse(Map<String, Object> map) {
        useCaseLibrary=new UseCaseLibrary();
        String casename = String.valueOf(map.get("caseName"));
        if (!"".equals(casename)){
            useCaseLibrary.setCaseName(Convert.toStr(casename,StrUtil.EMPTY));
        }
        String deviceType = String.valueOf(map.get("deviceType"));
        if (!"".equals(deviceType)){
            useCaseLibrary.setDeviceType(Convert.toStr(deviceType,StrUtil.EMPTY));
        }
        String lastdate = String.valueOf(map.get("lastdate"));
        if (!"".equals(lastdate)){
            useCaseLibrary.setTime(Convert.toDate(lastdate,null));
        }
        String caseslist = String.valueOf(map.get("casesList"));
        if (!"".equals(caseslist)){
            caseslist=caseslist.substring(1,caseslist.length()-1);
            List<String> list = Arrays.asList(caseslist.split(","));
            useCaseLibrary.setCasesName(list);
        }
        String tasklist = String.valueOf(map.get("tasklist"));
        if (!"".equals(tasklist)){
            tasklist=tasklist.substring(1,caseslist.length()-1);
            referenceTasks = Arrays.asList(tasklist.split(","));
        }else{
            referenceTasks=new ArrayList<>();
        }
        String referencenum = String.valueOf(map.get("reference"));
        if (!"".equals(referencenum)){
            referenceNum=Convert.toStr(referencenum,StrUtil.EMPTY);
        }

    }

    /**
     * @description
     *     解析配置文件
     * @param element
     * @return
     * @author zhaoxin
     * @date 2019/8/12 14:34
     */
    public void  parse(Element element) {
        useCaseLibrary=new UseCaseLibrary();
        Node caseNameNode = Dom4jUtil.getNodeByXPath("casename", element);
        if (caseNameNode != null) {
            useCaseLibrary.setCaseName(Convert.toStr(caseNameNode.getText(), StrUtil.EMPTY));
        }
        Node caseNameUUIDNode = Dom4jUtil.getNodeByXPath("casenameuuid", element);
        if (caseNameUUIDNode != null) {
            useCaseLibrary.setCaseNameUUID(Convert.toStr(caseNameUUIDNode.getText(), StrUtil.EMPTY));
        }
        Node deviceTypeNode = Dom4jUtil.getNodeByXPath("devicetype", element);
        if (deviceTypeNode != null) {
            useCaseLibrary.setDeviceType( Convert.toStr(deviceTypeNode.getText(), StrUtil.EMPTY));
        }
        Node modeNode = Dom4jUtil.getNodeByXPath("mode", element);
        if (modeNode != null) {
            useCaseLibrary.setMode( Convert.toStr(modeNode.getText(), StrUtil.EMPTY));
        }
        Node casesListNode = Dom4jUtil.getNodeByXPath("caseslist", element);
        if (casesListNode != null&&!"".equals(casesListNode.getText())) {
            String text = casesListNode.getText();
            text=text.substring(1,text.length()-1);
            List<String> list = Arrays.asList(text.split(","));
            useCaseLibrary.setCasesName(list);
        }
        //referencetask
        Node taskListNode = Dom4jUtil.getNodeByXPath("referencetask", element);
        if (taskListNode != null&&!"".equals(taskListNode.getText())) {
            String text = taskListNode.getText();
            text=text.substring(1,text.length()-1);
            referenceTasks = Arrays.asList(text.split(","));
        }else {
            referenceTasks=new ArrayList<>();
        }
        Node referenceNode = Dom4jUtil.getNodeByXPath("reference", element);
        if (referenceNode != null) {
            referenceNum = Convert.toStr(referenceNode.getText(), StrUtil.EMPTY);
        }
        Node lastdateNode = Dom4jUtil.getNodeByXPath("lastdate", element);
        if (lastdateNode != null) {
            useCaseLibrary.setTime(DateUtil.parse(lastdateNode.getText()));
        }
    }
    public UseCaseLibrary getUseCaseLibrary() {
        return useCaseLibrary;
    }

    public void setUseCaseLibrary(UseCaseLibrary useCaseLibrary) {
        this.useCaseLibrary = useCaseLibrary;
    }

    public String getReferenceNum() {
        return referenceNum;
    }

    public void setReferenceNum(String referenceNum) {
        this.referenceNum = referenceNum;
    }

    public List<String> getReferenceTasks() {
        return referenceTasks;
    }

    public void setReferenceTasks(List<String> referenceTasks) {
        this.referenceTasks = referenceTasks;
    }

    @Override
    public String toString() {
        return "UseCaseLibraryDto{" +
                "useCaseLibrary=" + useCaseLibrary +
                ", referenceNum='" + referenceNum + '\'' +
                ", referenceTasks=" + referenceTasks +
                '}';
    }
}
