package cn.com.entity;

import cn.com.common.Const;
import cn.com.interceptor.UserSession;
import cn.com.util.Dom4jUtil;
import cn.com.util.TestThread;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.io.File;
import java.util.*;

/**
 * @description
 *        检测任务
 * @author zhaoxin
 * @date 2019/8/8 18:01
 */
public class DetectionTask {

    /**就绪状体 没有报告记录且调用工具status返回值为1*/
    public static final int  STATE_READY=-1;

    /**运行中  */
    public static final int  STATE_RUNNING=0;

    /**完成 调用工具status返回值为1*/
    public static final int  STATE_NOT_RUNNING=1;

    /**运行异常*/
    public static final int  STATE_ERROR=2;



    /**任务编号*/
    private String taskNum;

    /**简介*/
    private String brief;

    /**CPU*/
    private String cpu;

    /**上次测试时间*/
    @JSONField(format ="yyyy-MM-dd HH:mm:ss")
    private Date lastDate;
    /**上次修改时间*/
    @JSONField(format ="yyyy-MM-dd HH:mm:ss")
    private Date lastModifyDate;

    /**测试状态*/
    private Integer state;


    /**是否有效 配置文件有 检测数据目录也存在 默认 false*/
    private boolean valid;
    /**是否已经是执行完成的 默认 false*/
    private boolean finish;


    
    /**
     *厂商名称
     */
    private String manCompany;
    /**
     * 用例库
     */
    private String caseLib;

    public void toXml(Element root) {
        root = Dom4jUtil.addChild(root,"detetiontask");
        root.addAttribute("name",taskNum);
        root.addComment("任务编号");
        Element numNode = Dom4jUtil.addChild(root, "tasknum");
        numNode.addText(Convert.toStr(taskNum, StrUtil.EMPTY));
        root.addComment("任务简介");
        Element briefNode = Dom4jUtil.addChild(root, "brief");
        briefNode.addText(Convert.toStr(brief, StrUtil.EMPTY));
        root.addComment("上次测试时间（yyyy-MM-dd hh:mm:ss）");
        Element lastNode = Dom4jUtil.addChild(root, "lastdate");
        if (lastDate==null){
            lastNode.addText(StrUtil.EMPTY);
        }else {
            lastNode.addText(DateUtil.format(lastDate,"yyyy-MM-dd HH:mm:ss"));
        }
        root.addComment("最后修改时间（yyyy-MM-dd HH:mm:ss）");
        Element lastModifyNode = Dom4jUtil.addChild(root, "lastmodifydate");
        if (lastModifyDate==null){
            lastModifyNode.addText(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        }else {
            lastModifyNode.addText(DateUtil.format(lastModifyDate,"yyyy-MM-dd HH:mm:ss"));
        }
        root.addComment("测试任务状态 0:运行中 1:未运行或停止 2:运行异常");
        Element stateNode = Dom4jUtil.addChild(root, "state");
        stateNode.addText(Convert.toStr(state,STATE_NOT_RUNNING+""));
        root.addComment("cpu");
        Element libNode = Dom4jUtil.addChild(root, "cpu");
        libNode.addText(Convert.toStr(cpu, StrUtil.EMPTY));
        Element caselibNode = Dom4jUtil.addChild(root, "casename");
        caselibNode.addText(Convert.toStr(caseLib, StrUtil.EMPTY));
        Element manCompanyNode = Dom4jUtil.addChild(root, "mancompany");
        manCompanyNode.addText(Convert.toStr(manCompany, StrUtil.EMPTY));
    }
    /**
     * @description
     *     实体类转化配置文件
     * @return Document
     * @author zhaoxin
     * @date 2019/8/12 14:15
     */
    public Document toXml(){
        String detectionPath= Const.WEB_CONFIG_PATH +"detectionTask.xml";
        File detectionFile=new File(detectionPath);
        Document document;
        Element rootElement;
        try {
            document = Dom4jUtil.readXML(detectionFile);
            rootElement = document.getRootElement();
            if("".equals(taskNum)||taskNum!=null){
                String format= String.format("//*[@name=\"%s\"]", taskNum);
                List<Node> nodes = rootElement.selectNodes(format);
                for(Node node:nodes){
                    rootElement.remove(node);
                }
            }
        } catch (Exception ignore) {
            document = Dom4jUtil.createXml();
            rootElement = Dom4jUtil.addChild(document, "detection-config");
        }
        toXml(rootElement);
        return document;
    }
    /**
     * @description
     *     解析配置文件
     * @param map
     * @return
     * @author wangkai
     * @date 2019/8/16 14:34
     */
    public void parse(Map<String, Object> map) {
        String tasknum = String.valueOf(map.get("tasknum"));
        if (!"".equals(tasknum)){
            taskNum= Convert.toStr(tasknum, StrUtil.EMPTY);
        }
        String briefs = String.valueOf(map.get("brief"));
        if (!"".equals(briefs)){
            brief= Convert.toStr(briefs, StrUtil.EMPTY);
        }
        String lastdate = String.valueOf(map.get("lastdate"));
        if (!"".equals(lastdate) ){
            lastDate= DateUtil.parse(lastdate);
        }
        String lastModify =String.valueOf(map.get("lastmodifydate"));
        if (!"".equals(lastModify) ){
            lastModifyDate= DateUtil.parse(lastModify);
        }
        String status = String.valueOf(map.get("state"));
        if (!"".equals(status)){
            state= Convert.toInt(Integer.parseInt(status),STATE_NOT_RUNNING);
        }
       
       
        String casename = String.valueOf(map.get("casename"));
        if (!"".equals(casename)){
            caseLib = Convert.toStr(casename, StrUtil.EMPTY);
        }

        String mancompany = String.valueOf(map.get("mancompany"));
        if (!"".equals(mancompany)){
            manCompany= Convert.toStr(mancompany, StrUtil.EMPTY);
        }
    }

    /**
     * @description
     *     实体类转化配置文件
     * @return Document
     * @author zhaoxin
     * @date 2019/8/12 14:15
     */
    public Document toBasicInfoXml(){
        Date date=new Date();
        Document doc = Dom4jUtil.createXml();
        Element root = Dom4jUtil.addChild(doc,"basicInfo");
        root.addAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
        Element idNode = Dom4jUtil.addChild(root, "taskID");
        idNode.addText(Convert.toStr(taskNum, StrUtil.EMPTY));
        Element inspectorIDNode = Dom4jUtil.addChild(root, "InspectorID");
        inspectorIDNode.addText(Convert.toStr(UserSession.get("uuid"), StrUtil.EMPTY));
        Element inspectorNameNode = Dom4jUtil.addChild(root, "InspectorName");
        inspectorNameNode.addText(Convert.toStr(UserSession.get(Const.LOGINNAME), StrUtil.EMPTY));
        Element taskStartTimeNode = Dom4jUtil.addChild(root, "taskStartTime");
        taskStartTimeNode.addText(StrUtil.EMPTY);
        Element taskEndTimeNode = Dom4jUtil.addChild(root, "taskEndTime");
        taskEndTimeNode.addText(StrUtil.EMPTY);

        Element toolIDNode = Dom4jUtil.addChild(root, "toolID");
        toolIDNode.addText("0012");
        Element toolnameNode = Dom4jUtil.addChild(root, "toolname");
        toolnameNode.addText("签名验签服务器");
        Element productNameNode = Dom4jUtil.addChild(root, "productName");
        productNameNode.addText("签名验签服务器");
        Element productIDNode = Dom4jUtil.addChild(root, "productID");
        productIDNode.addText("null");
        Element productTypeNode = Dom4jUtil.addChild(root, "productType");
        productTypeNode.addText("签名验签服务器");
        Element manCompanyNode = Dom4jUtil.addChild(root, "manCompany");
        manCompanyNode.addText(Convert.toStr(manCompany,"签名验签服务器"));
        Element environmentInfoNode = Dom4jUtil.addChild(root, "environmentInfo");
        Element testEnvironmentIDNode= Dom4jUtil.addChild(environmentInfoNode,"testEnvironmentID");
        testEnvironmentIDNode.addText("环境一");
        Element testEnvironmentInfoNode= Dom4jUtil.addChild(environmentInfoNode,"testEnvironmentInfo");
        Element equipmentTypeNode= Dom4jUtil.addChild(testEnvironmentInfoNode,"equipmentType");
        equipmentTypeNode.addText("签名验签服务器");
        return doc;
    }


    /**
     * @description
     *     解析配置文件
     * @param obj
     * @return
     * @author zhaoxin
     * @date 2019/8/12 14:34
     */
    public void  parse( Object obj){
        Element root;
        if(obj instanceof Element){
            root=(Element) obj;
        }else if(obj instanceof Document) {
            root= ((Document) obj).getRootElement();
        }else {
            return;
        }
        Node numNode = Dom4jUtil.getNodeByXPath("tasknum",root);
        if (numNode!=null){
            taskNum= Convert.toStr(numNode.getText(), StrUtil.EMPTY);
        }
        Node briefNode = Dom4jUtil.getNodeByXPath("brief",root);
        if (briefNode!=null){
            brief= Convert.toStr(briefNode.getText(), StrUtil.EMPTY);
        }
        Node lastNode = Dom4jUtil.getNodeByXPath("lastdate",root);
        if (lastNode!=null && lastNode.getText()!=null&&!"".equals(lastNode.getText())){
            lastDate= DateUtil.parse(lastNode.getText());
        }
        Node lastModifyNode = Dom4jUtil.getNodeByXPath("lastmodifydate",root);
        if (lastModifyNode!=null && lastModifyNode.getText()!=null&&!"".equals(lastModifyNode.getText())){
            lastModifyDate= DateUtil.parse(lastModifyNode.getText());
        }
        Node stateNode = Dom4jUtil.getNodeByXPath("state",root);
        if (stateNode!=null){
            state= Convert.toInt(stateNode.getText(),STATE_NOT_RUNNING);
        }
        Node cpuNode = Dom4jUtil.getNodeByXPath("cpu",root);
        if (cpuNode!=null){
            cpu = Convert.toStr(cpuNode.getText(), StrUtil.EMPTY);
        }
        Node casenameNode = Dom4jUtil.getNodeByXPath("casename",root);
        if (casenameNode!=null){
            caseLib = Convert.toStr(casenameNode.getText(), StrUtil.EMPTY);
        }

        Node manCompanyNode = Dom4jUtil.getNodeByXPath("mancompany",root);
        if (casenameNode!=null){
            manCompany= Convert.toStr(manCompanyNode.getText(), StrUtil.EMPTY);
        }
        Node productnameNode = Dom4jUtil.getNodeByXPath("productname",root);


    }

    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public String getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(String taskNum) {
        this.taskNum = taskNum;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

   

  

    public String getManCompany() {
        return manCompany;
    }

    public void setManCompany(String manCompany) {
        this.manCompany = manCompany;
    }

    public String getCaseLib() {
        return caseLib;
    }

    public void setCaseLib(String caseLib) {
        this.caseLib = caseLib;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

   

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    @Override
    public String toString() {
        return "DetectionTask{" +
                "taskNum='" + taskNum + '\'' +
                ", brief='" + brief + '\'' +
                ", lastDate=" + lastDate +
                ", lastModifyDate=" + lastModifyDate +
                ", state=" + state +
                ", valid=" + valid +
                ", finish=" + finish +
                ", manCompany='" + manCompany + '\'' +
                ", caseLib='" + caseLib + '\'' +
                '}';
    }
}
