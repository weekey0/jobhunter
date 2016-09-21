package cn.csdb.organchem.MSDS.dao;

import cn.csdb.organchem.MSDS.bean.Classification;
import cn.csdb.organchem.MSDS.bean.Drug;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author code4crafer@gmail.com
 *         Date: 13-6-23
 *         Time: 下午4:27
 */
@Repository("riskClassificationDao")
public interface RiskClassificationDao {

    @InsertProvider(type = RiskClassificationDaoProvider.class, method = "insertAllRiskClassificationInfo")
    public int addClassifications(List<Classification> riskClassificationInfo);
    @Select("select count(id) from Classification where url=#{url}")
    public int selectClassificationsByUrl(String url);
    @InsertProvider(type =RiskClassificationDaoProvider.class,method ="insertAllDrug")
    public int addDrugs(@Param("drugList") List<Drug> drugList);
    @Select("select count(id) from Drug where url=#{url} and classificationTid=#{classificationTid} and classificationFtid=#{classificationFtid}")
    public int selectDrugByTidAndUrl(@Param("classificationFtid")int classificationFtid,@Param("classificationTid") int classificationTid,@Param("url") String url);
    @Select("select count(id) from Drug where url=#{drug.url} and classificationTid=#{drug.classificationTid} and classificationFtid=#{drug.classificationFtid}")
    public int hasDrug(@Param("drug")Drug drug);
}
