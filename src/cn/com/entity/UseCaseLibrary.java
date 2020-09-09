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
 * @Description:���������ʵ����
 */
public class UseCaseLibrary {
    /**
     * ����������
     */
    private String caseName;

    /**
     * �����������ļ���
     */
    @JSONField(serialize = false)
    private String caseNameUUID;

    /**
     * �����豸����
     */
    private String deviceType;

    /**
     * ģʽ
     */
    private String mode;

    /**
     * ����ѡ��
     */
    private List<CaseInfo> cases;
    /**
     * ѡ�����������
     */
    private List<String> casesName;
    /**
     * ����ʱ���
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
