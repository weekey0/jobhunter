package cn.csdb.organchem.MSDS.bean;


/**
 * @author week
 * @create 2016-09-19 14:49
 */
public class Classification {
    private int id;
    private int tid;
    private int ftid;
    private String url;
    private String source;
    private String classification;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getFtid() {
        return ftid;
    }

    public void setFtid(int ftid) {
        this.ftid = ftid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
}
