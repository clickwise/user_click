package cn.clickwise.clickad.feathouse;

//mysql table 的抽象
public class Table {

	private String name;

	public Table(String name){
		this.name=name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
