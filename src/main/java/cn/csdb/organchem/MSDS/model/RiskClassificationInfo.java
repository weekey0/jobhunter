package cn.csdb.organchem.MSDS.model;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

import java.util.List;


@TargetUrl(value ="http://www.organchem.csdb.cn/scdb/msds/msds_wxx_query.asp",sourceRegion = "")
@HelpUrl("http://www.organchem.csdb.cn/scdb/msds/msds_wxx_query.asp")
public class RiskClassificationInfo implements AfterExtractor {
    @ExtractBy("/html/body/table[1]/tbody/tr/td/b/a/text()|/html/body/table[1]/tbody/tr/td/a/text()")
    private List<String> classifications;
    @ExtractBy("/html/body/table[1]/tbody/tr/td/b/a/@href|/html/body/table[1]/tbody/tr/td/a/@href")
    private List<String> urls;
    private String source="scdb/msds/msds_wxx_query.asp";

    public void setClassifications(List<String> classifications) {
        this.classifications = classifications;
    }

    public List<String> getClassifications() {
        return classifications;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "RiskClassificationInfo{" +
                "classifications=" + classifications +
                ", urls=" + urls +
                ", source='" + source + '\'' +
                '}';
    }

    @Override
    public void afterProcess(Page page) {
        if (page.getRawText().contains("Error")){
            System.out.println("ip已经被封了..");
        }
        System.out.println("in afterProcess");
    }
}
