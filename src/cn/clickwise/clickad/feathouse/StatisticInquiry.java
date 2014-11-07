package cn.clickwise.clickad.feathouse;


/**
 * 查询dmp的统计信息
 * @author zkyz
 *
 */
public abstract class StatisticInquiry {

	public abstract StatisticStruct getDmpStatistic(Dmp dmp,int day);
	
	
}
