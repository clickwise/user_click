package cn.clickwise.clickad.profile;

/**
 * 用户的各种属性
 * @author zkyz
 */
public class Profile {

	private String gender="";
	
	private String age="";
	
	private String income="";

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}
	
	public String toString()
	{
		String str="";
		
		str="gender:"+gender+" "+"age:"+age+" income:"+income;
				
		return str;
	}
	
}
