package cn.clickwise.clickad.feathouse;

public class Area {
	private String name;
	
	private String areaCode;

	public Area(String name,String areaCode)
	{
	   this.name=name;
	   this.areaCode=areaCode;
	}
	
	public Area(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

}
