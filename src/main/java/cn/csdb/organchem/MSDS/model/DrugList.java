package cn.csdb.organchem.MSDS.model;

import cn.csdb.organchem.MSDS.RiskClassificationCrawler;
import cn.csdb.organchem.MSDS.bean.Drug;
import cn.csdb.organchem.MSDS.dao.RiskClassificationDao;
import cn.csdb.organchem.MSDS.pagePrecessor.ShowMoreSpider;
import cn.csdb.organchem.MSDS.util.TableUtil4Jsoup;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author week
 * @create 2016-09-19 17:26
 */
@TargetUrl(value ="http://www.organchem.csdb.cn/scdb/msds/msds_wxx_result.asp*",sourceRegion = "")
@HelpUrl("http://www.organchem.csdb.cn/scdb/msds/msds_wxx_query.asp")
public class DrugList implements AfterExtractor {

    private List<Drug> drugList;

    private Page page;

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Drug> getDrugList() {
        return drugList;
    }

    public void setDrugList(List<Drug> drugList) {
        this.drugList = drugList;
    }

    @Override
    public void afterProcess(Page page) {
        JSONArray json = TableUtil4Jsoup.create().analyseVerticalTable(Jsoup.parse(page.getHtml().get()),"table.newform","tr>td","tr>td");
        System.out.println("json = " + json);
        setPage(page);
        setDrugList(createDrugList(json,page));
        System.out.println("in afterProcess drug");
    }

    public static  List<Drug> createDrugList(JSONArray json, Page page) {
        List<Drug> result = new ArrayList<Drug>(json.size());
        for (Object o : json) {
            JSONObject jsont = (JSONObject) o;
            Drug drug = new Drug();
            drug.setCasNum(jsont.getString("CAS号"));
            if("POST".equals(page.getRequest().getMethod())){
                NameValuePair[] nameValuePair = ( NameValuePair[]) page.getRequest().getExtra("nameValuePair");
                for (NameValuePair valuePair : nameValuePair) {
                    if("wxx".equals(valuePair.getName())){
                        setTidAndFtid(drug,  valuePair.getValue());
                        break;
                    }
                }
            }else{
                setTidAndFtid(drug,page.getUrl().get().replaceAll(".*wxx=", ""));
            }
            drug.seteName(jsont.getString("化学品英文名称"));
            drug.setFormula(jsont.getString("分子式"));
            drug.setLanguage(jsont.getString("语言"));
            drug.setName(jsont.getString("化学品中文名称"));
            drug.setRiskClassification(jsont.getString("危险性分类"));
            List<Selectable> nodes = page.getHtml().xpath("/html/body/table/tbody/tr").nodes();
            for (Selectable node : nodes) {
                if (jsont.getString("序号").contains(node.xpath("td/text()").get())){
                    drug.setUrl(node.xpath("a/@href").get());
                    break;
                }
            }
            result.add(drug);
        }
        return result;
    }

    private static void setTidAndFtid(Drug drug, String t) {
        drug.setClassificationFtid(t.contains(".") ? Integer.parseInt(t.replaceFirst("\\..*", "")) : 0);
        drug.setClassificationTid(t.contains(".") ? Integer.parseInt(t.replaceFirst(".*\\.", "")) : Integer.parseInt(t));
    }


}
