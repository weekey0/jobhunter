package cn.csdb.organchem.MSDS.pipeline;

import cn.csdb.organchem.MSDS.bean.Classification;
import cn.csdb.organchem.MSDS.dao.RiskClassificationDao;
import cn.csdb.organchem.MSDS.model.RiskClassificationInfo;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author week
 * @create 2016-09-18 16:15
 */
@Component("riskClassificationPipeline")
public class RiskClassificationPipeline implements PageModelPipeline<RiskClassificationInfo> {
    @Resource
    private RiskClassificationDao riskClassificationDao;
    @Override
    public void process(RiskClassificationInfo riskClassificationInfo, Task task) {
        List<String> classifications =  riskClassificationInfo.getClassifications();
        List<String> urls = riskClassificationInfo.getUrls();
        String source = riskClassificationInfo.getSource();
        List<Classification> list = new ArrayList<Classification>(urls.size());
        for (int i = 0; i < urls.size(); i++) {
            if(1 > riskClassificationDao.selectClassificationsByUrl(urls.get(i))) {
                Classification classification = new Classification();
                classification.setClassification(classifications.get(i));
                classification.setSource(source);
                classification.setUrl(urls.get(i));
                String t = urls.get(i).replaceAll(".*wxx=", "");
                classification.setTid(t.contains(".") ? Integer.parseInt(t.replaceFirst(".*\\.", "")) : Integer.parseInt(t));
                classification.setFtid(t.contains(".") ? Integer.parseInt(t.replaceFirst("\\..*", "")) : 0);
                list.add(classification);
            }
        }
        int add =0;
        if (list.size()>0) {
           add= riskClassificationDao.addClassifications(list);
        }
        System.out.println("insert "+add);

    }
}
