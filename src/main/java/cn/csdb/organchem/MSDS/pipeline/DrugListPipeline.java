package cn.csdb.organchem.MSDS.pipeline;

import cn.csdb.organchem.MSDS.RiskClassificationCrawler;
import cn.csdb.organchem.MSDS.bean.Drug;
import cn.csdb.organchem.MSDS.dao.RiskClassificationDao;
import cn.csdb.organchem.MSDS.model.DrugList;
import cn.csdb.organchem.MSDS.pagePrecessor.ShowMoreSpider;
import cn.csdb.organchem.MSDS.scheduler.postScheduler;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.PageModelPipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author week
 * @create 2016-09-18 16:15
 */
@Component("drugListPipeline")
public class DrugListPipeline implements PageModelPipeline<DrugList> {
    @Resource
    private RiskClassificationDao riskClassificationDao;
    @Resource
    private ShowMoreSpider showMoreSpider;
    @Override
    public void process(DrugList drugList, Task task) {

        List<Drug> dlist =  drugList.getDrugList();
        getInsertList(riskClassificationDao,dlist);
        int add =0;
        if (dlist.size()>0) {
           add= riskClassificationDao.addDrugs(dlist);
        }
        System.out.println("insert drug "+add);
        System.out.println("drug otherPage");
        otherPageProcess(drugList.getPage());
        System.out.println("end drug Pipleline");
    }
    private void otherPageProcess(Page page) {
        try {
            Site site = RiskClassificationCrawler.site;
          //  PageProcessor spider = new ShowMoreSpider(,);
            showMoreSpider.setProxy("http://115.29.146.40:8088");
            showMoreSpider.setCookie(site.getCookies());
            int maxpage = Integer.parseInt(page.getHtml().xpath("/html/body/form/table/tbody/tr/allText()").get().split("/")[1].replaceAll("\\D.*", ""));
            Request[] requests = new Request[maxpage-1];

            for (int i = 2; i <= maxpage; i++) {
                Map nameValuePair = new HashMap();
                NameValuePair[] values = new NameValuePair[2];
                values[0] = new BasicNameValuePair("wxx", page.getUrl().get().replaceAll(".*wxx=", ""));
                values[1] = new BasicNameValuePair("pageno",i+"");
                nameValuePair.put("nameValuePair", values);
                Request request = new Request(page.getUrl().get().replaceFirst("\\?.*", ""));
                request.setMethod(HttpConstant.Method.POST);
                request.setExtras(nameValuePair);
                requests[i-2]=request;
            }
            Spider.create(showMoreSpider).thread(5).setScheduler(new postScheduler()).addRequest(requests).run();//先scheduler设置后在addRequest
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static List<Drug> getInsertList(RiskClassificationDao riskClassificationDao, List<Drug> dlist) {
        ArrayList<Drug> temp = new ArrayList<Drug>(dlist);
        for (Drug drug : temp) {
            if (riskClassificationDao.selectDrugByTidAndUrl(drug.getClassificationFtid(),drug.getClassificationTid(),drug.getUrl())>0) {
                dlist.remove(drug);
            }
        }
        return dlist;
    }
}
