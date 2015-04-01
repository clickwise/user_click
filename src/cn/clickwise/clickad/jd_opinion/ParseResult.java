package cn.clickwise.clickad.jd_opinion;

public class ParseResult {

	private String keyword;
	
	private String link;
	
	public ParseResult(){
		
	}
	
	public ParseResult(String keyword,String link)
	{
		this.setKeyword(keyword);
		this.setLink(link);
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
}
