package cn.clickwise.clickad.jd_opinion;

import java.net.URLDecoder;

import cn.clickwise.lib.string.SSO;

public class ParseResult {

	private String uwword="";
	private String uuword="";
	private String uqword="";
	private String rwword="";
	private String ruword="";
    private String rqword="";
	
	private String link;
		
	public ParseResult(){
		
	}
	
	public boolean isNull()
	{
	  if(uwword.indexOf("%")<0&&uuword.indexOf("%")<0&&uqword.indexOf("%")<0&&rwword.indexOf("%")<0&&ruword.indexOf("%")<0&&rqword.indexOf("%")<0)
	  {
		  return true;
	  }
	  return false;
	}
	
	public boolean isInValid()
	{
		 if(uwword.getBytes().length==uwword.length()&&uuword.getBytes().length==uuword.length()&&uqword.getBytes().length==uqword.length()&&rwword.getBytes().length==rwword.length()&&ruword.getBytes().length==ruword.length()&&rqword.getBytes().length==rqword.length())
		 {
			  return true;
		 }
		  
		return false;
	}

	public void decode()
	{
		 if(SSO.tnoe(uwword))
		 {
			 uwword=URLDecoder.decode(uwword);
		 }
		    
		 if(SSO.tnoe(uuword))
		 {
			 uuword=URLDecoder.decode(uuword);
		 }
		    
		 if(SSO.tnoe(uqword))
		 {
			 uqword=URLDecoder.decode(uqword);
		 }
		    
		 if(SSO.tnoe(rwword))
		 {
			 rwword=URLDecoder.decode(rwword);
		 }
		    
		 if(SSO.tnoe(ruword))
		 {
		     ruword=URLDecoder.decode(ruword);
		  }
		    
		  if(SSO.tnoe(rqword))
		  {
			 rqword=URLDecoder.decode(rqword);
		  }
	}
	
	public String toString(){
		
		   String str="";
		   if(SSO.tnoe(uwword))
		    {
		    	str=(str+uwword)+";";
		    }
		    
		    if(SSO.tnoe(uuword))
		    {
		        str=(str+uuword)+";";
		    }
		    
		    if(SSO.tnoe(uqword))
		    {
		    	str=(str+uqword)+";";
		    }
		    
		    if(SSO.tnoe(rwword))
		    {
		    	str=(str+rwword)+";";
		    }
		    
		    if(SSO.tnoe(ruword))
		    {
		    	str=(str+ruword)+";";
		    }
		    
		    if(SSO.tnoe(rqword))
		    {
		    	str=(str+rqword)+";";
		    }
		    
		    return str;
	}
	
	public String toDeString()
	{
	    String str="";
	  
	    if(SSO.tnoe(uwword))
	    {
	    	str=(str+URLDecoder.decode(uwword))+";";
	    }
	    
	    if(SSO.tnoe(uuword))
	    {
	    	str=(str+URLDecoder.decode(uuword))+";";
	    }
	    
	    if(SSO.tnoe(uqword))
	    {
	    	str=(str+URLDecoder.decode(uqword))+";";
	    }
	    
	    if(SSO.tnoe(rwword))
	    {
	    	str=(str+URLDecoder.decode(rwword))+";";
	    }
	    
	    if(SSO.tnoe(ruword))
	    {
	    	str=(str+URLDecoder.decode(ruword))+";";
	    }
	    
	    if(SSO.tnoe(rqword))
	    {
	    	str=(str+URLDecoder.decode(rqword))+";";
	    }
	    
	    return str;
	}
	
	public ParseResult(String keyword,String link)
	{
		this.setLink(link);
	}

	public String getUwword() {
		return uwword;
	}

	public void setUwword(String uwword) {
		this.uwword = uwword;
	}
	
	public String getUuword() {
		return uuword;
	}

	public void setUuword(String uuword) {
		this.uuword = uuword;
	}

	public String getUqword() {
		return uqword;
	}

	public void setUqword(String uqword) {
		this.uqword = uqword;
	}
	
	public String getRwword() {
		return rwword;
	}

	public void setRwword(String rwword) {
		this.rwword = rwword;
	}
	
	public String getRuword() {
		return ruword;
	}

	public void setRuword(String ruword) {
		this.ruword = ruword;
	}
	
	public String getRqword() {
		return rqword;
	}

	public void setRqword(String rqword) {
		this.rqword = rqword;
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
}
