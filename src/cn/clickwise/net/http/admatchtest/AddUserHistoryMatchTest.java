package cn.clickwise.net.http.admatchtest;

import java.util.Properties;

import cn.clickwise.liqi.str.configutil.ConfigFileReader;

public class AddUserHistoryMatchTest extends AdMatchTestBase{

	public String method="/adduserhistorymatch?";
	public String suffix="";

	public void testAddUserHistoryMatch(String uid,String adid,String similarity,String datatype,String infotype,String adinfotype)
	{
		String url=url_prefix+method+"uid="+uid+"&adid="+adid+"&similarity="+similarity+"&datatype="+datatype+"&infotype="+infotype+"&adinfotype="+adinfotype;
		String response=hct.postUrl(url);
		System.out.println("response:"+response);
		
	}
	
	public static void main(String[] args)
	{
		AddUserHistoryMatchTest auhmt=new AddUserHistoryMatchTest();
		String uid="521d0256e1682b6bf5edafd83370b7df";
		String adid="test_23";
		String similarity="0.5";
		String datatype="BAIDU,TBSEARCH,HOSTTITLE";
		String infotype="cates,attrs,items,bdcates,bdkeys,refer_host,url_host,url_title,host_cate";
		String adinfotype="keywords";
		auhmt.testAddUserHistoryMatch(uid, adid, similarity, datatype, infotype, adinfotype);
		
	}
	
	
}
