package cn.clickwise.clickad.feathouse;

import java.sql.Date;

public class TimeRange {

	private Date startDay;
	private Date endDay;
	public TimeRange(Date startDay,Date endDay)
	{
		this.startDay=startDay;
		this.endDay=endDay;
	}
	
	public Date getStartDay() {
		return startDay;
	}
	
	public void setStartDay(Date startDay) {
		this.startDay = startDay;
	}
	
	public Date getEndDay() {
		return endDay;
	}
	
	public void setEndDay(Date endDay) {
		this.endDay = endDay;
	}
	
}
