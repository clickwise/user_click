package cn.clickwise.clickad.feathouse;

/**
 * 查询key
 * @author zkyz
 */
public class Key {
	
	public String key="";
	
	public String ip="";
	
	public String area="";
	
	public Key(String key)
	{
		this.key=key;
		this.ip="";
		this.area="";
	}
	
	public Key(String key,String ip)
	{
		this.key=key;
		this.ip=ip;
		this.area="";
	}
	
	public Key(String key,String ip,String area)
	{
		this.key=key;
		this.ip=ip;
		this.area=area;
	}
	
    public String toString()
    {
    	return this.key;
    }
}
