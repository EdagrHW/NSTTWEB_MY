package cn.com.entity;

import cn.com.util.RegexUtil;
import cn.com.util.UUIDUtils;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;


/**
 * @author WangKai
 * @ClassName: UseCaseLibrary
 * @date 2019-08-15 14:47
 * @Description:用例库管理实体类
 */
public class UseCaseLibrary {
    /**
     * 用例库名称
     */
    private String caseName;

    /**
     * 创建的用例文件夹
     */
    @JSONField(serialize = false)
    private String caseNameUUID;

    /**
     * 待测设备类型
     */
    private String deviceType;

    /**
     * 模式
     */
    private String mode;

    /**
     * 用例选择
     */
    private List<CaseInfo> cases;
    /**
     * 选择的用例名称
     */
    private List<String> casesName;
    /**
     * 创建时间点
     */
    @JSONField(format ="yyyy-MM-dd hh:mm:ss")
    private Date time;



    public String getCaseNameUUID() {
       return caseNameUUID;
    }

    public void setCaseNameUUID(String caseNameUUID) {
       this.caseNameUUID=caseNameUUID;
    }
    public void getUUID(){
        setCaseNameUUID(UUIDUtils.getUUID16());
    }
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<CaseInfo> getCases() {
        return cases;
    }

    public void setCases(List<CaseInfo> cases) {
        this.cases = cases;
    }

    public List<String> getCasesName() {
        return casesName;
    }

    public void setCasesName(List<String> casesName) {
        this.casesName = casesName;
    }

    @Override
    public String toString() {
        return "UseCaseLibrary{" +
                "caseName='" + caseName + '\'' +
                ", caseNameUUID='" + caseNameUUID + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", mode='" + mode + '\'' +
                ", cases=" + cases +
                ", casesName=" + casesName +
                ", time='" + time + '\'' +
                '}';
    }
}
