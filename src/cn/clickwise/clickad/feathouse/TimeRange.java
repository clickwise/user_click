package cn.clickwise.clickad.feathouse;

import java.sql.Date;
import java.util.ArrayList;

import cn.clickwise.lib.time.TimeOpera;

public class TimeRange {

	private Date startDay;
	private Date endDay;
	
	public TimeRange(Date startDay,Date endDay)
	{
		this.startDay=startDay;
		this.endDay=endDay;
	}
	
	public Date[] listDays()
	{
		ArrayList<Date> list=new ArrayList<Date>();
		list.add(startDay);
		long start=startDay.getTime();
		long end=endDay.getTime();
			
		for(long time=start;time<end;time+=TimeOpera.getEntireDay())
		{
			Date newDay=new Date(time);
			list.add(newDay);
		}
			
		Date[] dates=new Date[list.size()];
		for(int i=0;i<list.size();i++)
		{
			dates[i]=list.get(i);
		}
		
		return dates;
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
