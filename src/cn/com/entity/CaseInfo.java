package cn.com.entity;

/**
 * @author WangKai
 * @ClassName: CaseInfo
 * @date 2019-09-02 14:43
 * @Description: ��Ҫ���������ģ�弰�޸ĵ���Ϣ
 */
public class CaseInfo {
    private String caseName;
    private String caseText;

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getCaseText() {
        return caseText;
    }

    public void setCaseText(String caseText) {
        this.caseText = caseText;
    }

    @Override
    public String toString() {
        return "CaseInfo{" +
                "caseName='" + caseName + '\'' +
                ", caseText='" + caseText + '\'' +
                '}';
    }
}
