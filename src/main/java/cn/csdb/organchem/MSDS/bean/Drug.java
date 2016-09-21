package cn.csdb.organchem.MSDS.bean;

/**
 * Created by week on 2016/9/20.
 */
public class Drug {
    private int id;
    private int classificationTid;
    private int classificationFtid;
    private String url;
    private String name;
    private String eName;
    private String casNum;
    private String formula;
    private String riskClassification;
    private String language;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassificationTid() {
        return classificationTid;
    }

    public void setClassificationTid(int classificationTid) {
        this.classificationTid = classificationTid;
    }

    public void setClassificationFtid(int classificationFtid) {this.classificationFtid = classificationFtid;}

    public int getClassificationFtid() {return classificationFtid;}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String getCasNum() {
        return casNum;
    }

    public void setCasNum(String casNum) {
        this.casNum = casNum;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getRiskClassification() {
        return riskClassification;
    }

    public void setRiskClassification(String riskClassification) {
        this.riskClassification = riskClassification;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
