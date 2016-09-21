package cn.csdb.organchem.MSDS;

import cn.csdb.organchem.MSDS.model.DrugList;
import cn.csdb.organchem.MSDS.model.RiskClassificationInfo;
import cn.csdb.organchem.MSDS.pipeline.DrugListPipeline;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 危险性分类检索
 *
 * @author week
 * @create 2016-09-18 16:13
 */
@Component
public class RiskClassificationCrawler {

    @Qualifier("riskClassificationPipeline")
    @Autowired
    private PageModelPipeline riskClassificationPipeline;
    @Qualifier("drugListPipeline")
    @Autowired
    private PageModelPipeline drugListPipeline;
    public static  Site site = Site.me().setCharset("gb2312").setHttpProxy(new HttpHost("115.29.146.40", 8088))
            .setRetryTimes(3).setSleepTime(1000).setUseGzip(true).setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");

    public void crawl() {
        Header[] cookieHeader = login("kleen", "123456");
        String[] coockie = cookieHeader[0].getValue().split("=");
        OOSpider.create(
                site.addCookie(coockie[0], coockie[1])
                , riskClassificationPipeline,RiskClassificationInfo.class).addPageModel(drugListPipeline,DrugList.class).
                addUrl("http://www.organchem.csdb.cn/scdb/msds/msds_wxx_query.asp").thread(5).run();
    }

    private final String loginUrl = "http://www.organchem.csdb.cn/scdb/main/slogin.asp";

    private  Header[] login(String userName, String password) {
        HttpResponse resp = null;
        HttpPost post = new HttpPost(URI.create("http://www.organchem.csdb.cn/scdb/main/left-3.asp"));
        BasicCookieStore cookieStore = new BasicCookieStore();
        HttpClientBuilder builder = HttpClientBuilder.create().disableContentCompression()
                .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy()).setUserAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.137 Safari/537.36");
        builder.addInterceptorLast(new RequestAcceptEncoding());
        builder.setDefaultCookieStore(cookieStore);
        builder.setProxy(new HttpHost("115.29.146.40", 8088));
        CloseableHttpClient client = builder.build();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Username", userName));
        params.add(new BasicNameValuePair("Password", password));
        params.add(new BasicNameValuePair("login", "登录"));////"%B5%C7%C2%BC"
        Header[] headers=null;
        try {
            resp = client.execute(post);
            if (resp != null && 200 == resp.getStatusLine().getStatusCode()) {
                headers = resp.getHeaders("Set-Cookie");
                headers[0].getName();
            }
            post.setURI(URI.create(loginUrl));
            post.setEntity(new UrlEncodedFormEntity(params,"gb2312"));
            resp = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean login = validateLogin(resp, post, client, headers);
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return login?headers:null;
    }

    private boolean validateLogin(HttpResponse resp, HttpPost post, CloseableHttpClient client, Header[] headers) {
        boolean result = false;
        try {
            if (resp != null && 200 == resp.getStatusLine().getStatusCode()) {
                if (headers == null) {
                    headers = resp.getHeaders("Set-Cookie");
                }
                resp = client.execute(post);
                result = validateLogin(resp,post,client,headers);
//                EntityUtils.toString(resp.getEntity(), "gb2312");
            }else if(resp!=null && 302 ==resp.getStatusLine().getStatusCode()){
                result = true;
            }else{
                System.out.println("...error response "+resp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext*.xml");
        final RiskClassificationCrawler riskClassificationCrawler = applicationContext.getBean(RiskClassificationCrawler.class);
        riskClassificationCrawler.crawl();
    }
}

