package cn.csdb.organchem.MSDS.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by week on 2016/9/20.
 */
public class TableUtil4Jsoup {
    protected Logger logger= LoggerFactory.getLogger(getClass());
    private static  TableUtil4Jsoup tableUtil4Jsoup;
    public static TableUtil4Jsoup create(){
        return tableUtil4Jsoup==null? tableUtil4Jsoup=new TableUtil4Jsoup():tableUtil4Jsoup;
    }
    /**
     * 解析常规表格，自动选取document的table（document.select("table tr:has(*)")）
     * <p>并且过滤掉其中table中Elements少于2个element元素。
     * <P>如果 过滤的元素<b>小于</b>过滤后元素 认为没有数据,返回null
     * <p>选中过滤后元素的直接子元素th（titles）的数量大于0,且能整除过滤后元素的直接子元素td（bodys）则认为存在表头（目前仅为横向表）并对应解析（analyseVerticalTable()）
     * <p>否则尝试以k-v格式解析表格 （analyseKeyValueTable()）
     * @param document
     * @return
     */
    public JSON analyseOneTable(Document document) {
        JSON result = null;
        if (document!=null) {
            Elements select = document.select("table tr:has(*)").clone();
            if(select.size()>0){
                Elements temp = filterLess2Elements(select);
                if((select.size()-temp.size())<temp.size()){
                    Elements titles = temp.select("tr >th");
                    Elements body1 = temp.get(1).select("tr >td");
                    if(titles.size()>0 && body1.size()==titles.size()){
                        logger.debug("表格存在表头");
                        result = analyseVerticalTable(titles, temp,"tr >td");

                    }else{
                        logger.debug("尝试以k-v格式解析");
                        result = analyseKeyValueTable(temp);
                    }
                }else{
                    logger.debug("无数据。。{}",select.html());
                }
            }
        }
        return result;
    }
    /**
     * 解析垂直表格
     * <pre>
     * ————————————————————————————————
     * | title | title | title | title |
     * |content|content|content|content|
     * |content|content|content|content|
     * |content|content|content|content|
     * |content|content|content|content|
     * —————————————————————————————————
     *
     * <pre>
     * @param document
     *  @param tableSelect
     *  @param titleSelect
     *  @param bodySelect
     * @return
     */
    public final JSONArray analyseVerticalTable(Document document,String tableSelect,String titleSelect,String bodySelect) {
        JSONArray result = null;
        if (document!=null) {
            Elements select = document.select(tableSelect).select("tr:has(*)").clone();
            if(select.size()>0){
                Elements temp = filterLess2Elements(select);
                if((select.size()-temp.size())<temp.size()){
                    Elements titles = temp.get(0).select(titleSelect);
                    temp.remove(0);
                   // Elements bodys = temp.select(bodySelect);//jsoup会把同样element保留一个
                    result = analyseVerticalTable(titles, temp,bodySelect);
                }else{
                    logger.debug("无数据。。{}",select.html());
                }
            }
        }
        return result;
    }
    /**
     * 解析表格
     * <pre>
     * ——————————
     * |key|word|
     * |key|word|
     * |key|word|
     * |key|word|
     * |key|word|
     * |key|word|
     * ——————————
     * </pre>
     * @param temp
     * @return
     */
    public final JSONObject analyseKeyValueTable(Elements temp) {
        JSONObject result = new JSONObject();
        for (Element element : temp) {
            Elements tds = element.select("tr >*");
            for (int i = 0; i < (tds.size()%2==0?tds.size():tds.size()-1); i+=2) {
                putInJson(result, tds.get(i), tds.get(i+1));
            }
        }
        return result;
    }

    /**
     * 解析垂直表格
     * <pre>
     * ————————————————————————————————
     * | title | title | title | title |
     * |content|content|content|content|
     * |content|content|content|content|
     * |content|content|content|content|
     * |content|content|content|content|
     * —————————————————————————————————
     *
     * <pre>
     * @param titles
     * @param temp
     * @param bodySelect
     * @return
     */
    public final JSONArray analyseVerticalTable(Elements titles, Elements temp,String bodySelect) {
        JSONArray result = new JSONArray();
        for (Element element : temp) {
            for (int i = 0; i < element.select(bodySelect).size()/titles.size(); i++) {
                JSONObject object = new JSONObject();
                for (int j = 0; j < titles.size(); j++) {
                    putInJson(object, titles.get(j), element.select(bodySelect).get(i*titles.size()+j));
                }
                result.add(object);
            }
        }
        return result;
    }

    /**
     * 过滤Element少于2个元素的Elements
     * @param select
     * @return
     */
    private final Elements filterLess2Elements(Elements select) {
        Elements temp = new Elements();
        for (int i=0;i<select.size();i++) {
            if(select.get(i).getAllElements().size()<=2) {
                logger.debug("过滤的Element {}",select.get(i).html());
            }else{
                temp.add(select.get(i));
            }
        }
        return temp;
    }
    /**
     * 尝试把keyE保存为全部（keyE.html()）,valueE保存正确值（ 优先级valueE.text()> valueE.getAllElements().attr("value") > valueE.html() ）
     * @param tempJson
     * @param keyE
     * @param valueE
     */
    private final void putInJson(JSONObject tempJson, Element keyE, Element valueE) {
        String key = keyE.html().replaceAll("[:：]", "").replaceAll("&nbsp;", "").trim();
        if(StringUtils.isNoneBlank(valueE.text())){
            tempJson.put(key.replaceAll("\u00A0", ""),valueE.text().replaceAll("\u00A0", ""));
        }else if(valueE.getAllElements().hasAttr("value")){
            tempJson.put(key.replaceAll("\u00A0", ""),valueE.getAllElements().attr("value").replaceAll("\u00A0", ""));
        }else{
            tempJson.put(key.replaceAll("\u00A0", ""),valueE.html().replaceAll("\u00A0", ""));
        }
    }
    /**
     * 合并source中指定appendKey的
     * key&value到source中
     * @param target 合并目标
     * @param source 合并源
     * @param compareKey 关联key
     * @param appendKey 要合并的key
     * @param appendValueFormat 合并格式format， 同System.out.printf(format, args),args固定为appendKey对应value值
     */
    public void mergeListMap(List<Map<String, String>> target, List<Map<String, String>> source,
                                String compareKey, String appendKey, String appendValueFormat) {
        if (StringUtils.isAnyBlank(appendValueFormat)) {
            appendValueFormat="%s";
        }
        for (Map<String,String> tmap : target) {
            for (Map<String, String> smap : source) {
                if(smap.get(compareKey).equals(tmap.get(compareKey))){
                    Formatter formatter = new Formatter(new StringBuilder());
                    String appendValue = formatter.format(Locale.getDefault(),appendValueFormat, smap.get(appendKey)).toString();
                    tmap.put(appendKey, appendValue);
                    break;
                }
            }
        }
    }
}
