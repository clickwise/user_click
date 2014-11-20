package cn.clickwise.web;

/**
 * 网页的抽象 
 * @author zkyz
 *
 */
public class WebAbstract {

	private String title;
	
	private String keywords;
	
	private String description;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString()
	{
		String str="";
		str=str+" title:"+title+"\n"
			   +" keywords:"+keywords+"\n"
			   +" description:"+description;
		
		return str;
	}
	
}
