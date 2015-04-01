package cn.clickwise.clickad.jd_opinion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import cn.clickwise.lib.string.SSO;

/**
 * 解析搜索连接，提取有用的信息
 * 
 * @author zkyz
 */
public class SEUrlParse {

	public static ParseResult parseItem(String line) {

		String host="";
		String url="";
		String refer="";
		
		ParseResult pr=new ParseResult();
		String[] fields=line.split("\001");
		if(fields.length<11)
		{
			return null;
		}
		
		host=fields[5];
		url=fields[6];
		refer=fields[7];
		
		if((SSO.tioe(url))&&(SSO.tioe(refer)))
		{
			return null;
		}
		
		String uwword=SSO.midfstrs(url, "wd=", "&");
		String uuword=SSO.midfstrs(url, "word=", "&");
		String uqword=SSO.midfstrs(url, "query", "&");
		
		String rwword=SSO.midfstrs(refer, "wd=", "&");
		String ruword=SSO.midfstrs(refer, "word=", "&");
		String rqword=SSO.midfstrs(refer, "query", "&");
		
		String rs="";
		if(SSO.tnoe(uwword))
		{
			rs=rs+"uwd="+uwword+";";
		}
		
		if(SSO.tnoe(uuword))
		{
			rs=rs+"uud="+uuword+";";
		}
		
		if(SSO.tnoe(uqword))
		{
			rs=rs+"uqd="+uqword+";";
		}
		
		if(SSO.tnoe(rwword))
		{
			rs=rs+"rwd="+rwword+";";
		}
		
		if(SSO.tnoe(ruword))
		{
			rs=rs+"rud="+ruword+";";
		}
		
		if(SSO.tnoe(rqword))
		{
			rs=rs+"rqd="+rqword+";";
		}
		
	    pr.setKeyword(rs);
	    
		return pr;
	}

	public static void parseStd() {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String line = "";
		ParseResult pr=null;

		try {
			while ((line = br.readLine()) != null) {
               pr=parseItem(line);
               if(pr==null)
               {
            	   continue;
               }
               
               pw.println(pr.getKeyword());
			}
			
			br.close();
			pw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
