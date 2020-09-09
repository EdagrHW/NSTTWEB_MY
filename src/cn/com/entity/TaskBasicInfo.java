package cn.com.entity;


/**
 * @author WangKai
 * @ClassName: BasicInfo
 * @date 2019-08-20 11:58
 * @Description:任务配置基本信息
 */
public class TaskBasicInfo {
    private String taskStartTime;
    private String taskEndTime;
    private String toolID;
    private String toolName;
    private String productName;
    private String productID;
    private String productType;

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public String getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getToolID() {
        return toolID;
    }

    public void setToolID(String toolID) {
        this.toolID = toolID;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    @Override
    public String toString() {
        return "TaskBasicInfo{" +
                "taskStartTime='" + taskStartTime + '\'' +
                ", taskEndTime='" + taskEndTime + '\'' +
                ", toolID='" + toolID + '\'' +
                ", toolName='" + toolName + '\'' +
                ", productName='" + productName + '\'' +
                ", productID='" + productID + '\'' +
                ", productType='" + productType + '\'' +
                '}';
    }
}
