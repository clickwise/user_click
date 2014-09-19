package cn.clickwise.clickad.feathouse;

import java.util.Date;

/**
 * 不同地区uid(cookie)查询数统计记录
 * @author zkyz
 */
public class StatisticsRecord {

	private String area;
	private Date date;
	private int uv;
	private int pv;
	
	public StatisticsRecord(String area,Date date,int uv,int pv)
	{
		this.area=area;
		this.date=date;
		this.uv=uv;
		this.pv=pv;
	}
	
	public String getArea() {
		return area;
	}
	
	public void setArea(String area) {
		this.area = area;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public int getUv() {
		return uv;
	}
	
	public void setUv(int uv) {
		this.uv = uv;
	}
	
	public int getPv() {
		return pv;
	}
	
	public void setPv(int pv) {
		this.pv = pv;
	}
	
	
}
