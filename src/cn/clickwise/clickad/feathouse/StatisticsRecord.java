package cn.clickwise.clickad.feathouse;

import java.util.Date;

/**
 * 不同地区uid(cookie)查询数统计记录
 * @author zkyz
 */
public class StatisticsRecord {

	private Area area;
	private Date date;
	private int uv;
	private int pv;
	
	public StatisticsRecord(Area area,Date date,int uv,int pv)
	{
		this.setArea(area);
		this.date=date;
		this.uv=uv;
		this.pv=pv;
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


	public Area getArea() {
		return area;
	}


	public void setArea(Area area) {
		this.area = area;
	}
	
	
}
