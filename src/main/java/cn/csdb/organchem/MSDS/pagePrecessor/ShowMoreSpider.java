package cn.csdb.organchem.MSDS.pagePrecessor;

import cn.csdb.organchem.MSDS.bean.Drug;
import cn.csdb.organchem.MSDS.dao.RiskClassificationDao;
import cn.csdb.organchem.MSDS.model.DrugList;
import cn.csdb.organchem.MSDS.pipeline.DrugListPipeline;
import cn.csdb.organchem.MSDS.util.TableUtil4Jsoup;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component("showMoreSpider")
public class ShowMoreSpider implements PageProcessor
{
    private Site site= Site.me().setRetryTimes(3).setSleepTime(2000).setTimeOut(10 * 1000).setCharset("gb2312")
    .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
    @Autowired
    private RiskClassificationDao riskClassificationDao;

    public ShowMoreSpider(){
        this(null);
    }
    public  ShowMoreSpider(String proxyStr, Map<String, String> cookie)
    {
        setProxy(proxyStr);
        setCookie(cookie);
    }



    public  ShowMoreSpider(Map<String, String> cookie)
    {
        this(null,cookie);
    }
    public void setProxy(String proxyStr) {
        if (StringUtils.isNoneBlank(proxyStr)) {
            String[] tmp = proxyStr.split(":");
            HttpHost proxy = new HttpHost(tmp[1].substring(2), Integer.parseInt(tmp[2]), tmp[0]);
            site.setHttpProxy(proxy);
        }
    }
    public void setCookie( Map<String, String> cookie){
        if (cookie!=null) {
            for (Map.Entry<String, String> co : cookie.entrySet()) {
                site.addCookie(co.getKey(), co.getValue());
            }
        }
    }
    @Override
    public void process(Page page)
    {
        JSONArray jsonArray = TableUtil4Jsoup.create().analyseVerticalTable(Jsoup.parse(page.getHtml().get()), "table.newform", "tr>td", "tr>td");
        List<Drug> dlist = DrugList.createDrugList(jsonArray, page);
        DrugListPipeline.getInsertList(riskClassificationDao,dlist);
        int add =0;
        if (dlist.size()>0) {
            add= riskClassificationDao.addDrugs(dlist);
        }
        System.out.println("ShowMoreSpider.process end insert drug "+add);
    }

    @Override
    public Site getSite()
    {
        return site;
    }
}
//class myhcDownloader extends HttpClientDownloader {
//    @Override
//    protected RequestBuilder selectRequestMethod(Request request) {
//        return super.selectRequestMethod(request);
//    }
//}