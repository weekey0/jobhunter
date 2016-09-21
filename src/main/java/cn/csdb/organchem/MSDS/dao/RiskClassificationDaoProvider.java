package cn.csdb.organchem.MSDS.dao;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * @author week
 * @create 2016-09-19 14:47
 */
public class RiskClassificationDaoProvider {
    public String insertAllDrug(Map params) {
        List list = (List) params.get("drugList");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO Drug ");
        sb.append("(classificationTid,classificationFtid,url,name,eName,casNum,formula,riskClassification,language) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat("(#'{'drugList[{0}].classificationTid},#'{'drugList[{0}].classificationFtid},#'{'drugList[{0}].url},#'{'drugList[{0}].name}, #'{'drugList[{0}].eName}," +
                " #'{'drugList[{0}].casNum},#'{'drugList[{0}].formula},#'{'drugList[{0}].riskClassification},#'{'drugList[{0}].language})");
        for (int i = 0; i < list.size(); i++) {
            sb.append(mf.format(new String[]{String.valueOf(i)}));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    public String insertAllRiskClassificationInfo(Map params) {
        List list = (List) params.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO Classification ");
        sb.append("(tid,ftid,name,url,source) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat("(#'{'list[{0}].tid},#'{'list[{0}].ftid},#'{'list[{0}].classification}, #'{'list[{0}].url}, #'{'list[{0}].source})");
        for (int i = 0; i < list.size(); i++) {
            //sb.append(mf.format(new Object[]{i}));
            sb.append(mf.format(new String[]{String.valueOf(i)}));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
