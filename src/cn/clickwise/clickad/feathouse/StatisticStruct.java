package cn.clickwise.clickad.feathouse;

/**
 * 统计数据结构体
 * @author zkyz
 */
public class StatisticStruct {

	private int pv=0;
	
	private int uv=0;
	
	private int ip=0;
	
	private String codeOfArea;
	
	private int day;

	public int getPv() {
		return pv;
	}

	public void setPv(int pv) {
		this.pv = pv;
	}

	public int getUv() {
		return uv;
	}

	public void setUv(int uv) {
		this.uv = uv;
	}

	public int getIp() {
		return ip;
	}

	public void setIp(int ip) {
		this.ip = ip;
	}

	public String getCodeOfArea() {
		return codeOfArea;
	}

	public void setCodeOfArea(String codeOfArea) {
		this.codeOfArea = codeOfArea;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
	
	public String toString()
	{
		String str="<codeOfArea:"+codeOfArea+" day:"+day+" pv:"+pv+" uv:"+uv+" ip:"+ip+">";
		return str;
	}
	
	
}
