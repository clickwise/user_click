package cn.clickwise.clickad.feathouse;
/**
 * 用户记录的结构
 * @author zkyz
 */
public class Record {

	private String key;
	
	private String value;

	public Record(String key,String value)
	{
		this.key=key;
		this.value=value;
	}
	
	public String getKey() {
		return key;
	}

	public void setColumn(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString()
	{
		return "key="+key+",value="+value;
	}
	
}
