package cn.clickwise.net.http.admatchtest;

import java.net.URLEncoder;

import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.str.basic.SSO;

public class AddHostCateTest extends AdMatchTestBase{

	public String method="/addhostrecord?s=";
	public String suffix="&platform=adshow";
	
	public void testAddHostCate(String text)
	{
	//	String seg_s="{\"keywords\":[\"啫喱\",\"啫喱水\"],\"adid\":\"001\"}";
		String encode_seg_s=URLEncoder.encode(text);
		//System.out.println("encode_seg_s:"+encode_seg_s);
		encode_seg_s=encode_seg_s.replaceAll("\\s+", "");
		String url=url_prefix+method+encode_seg_s+suffix;
		String response=hct.postUrl(url);
		System.out.println("response:"+response);	
			
	}
	
	public String json_str(String host,String cate)
	{
		if(SSO.tioe(host)||SSO.tioe(cate))
		{
			return "";
		}
		String res="";
		res="{\"host\":\""+host+"\",\"cate\":\""+cate+"\",\"datatype\":\"ONE_HIER\"}";
		return res;
		
	}
	
	public static void main(String[] args) throws Exception
	{
		AddHostCateTest ahct=new AddHostCateTest();
		String[] recs=FileToArray.fileToDimArr("temp/host_cate/hcn.txt");
		String[] seg_arr=null;
		for(int i=0;i<recs.length;i++)
		{
			seg_arr=recs[i].trim().split("\001");
			if(seg_arr==null||seg_arr.length!=2)
			{
				continue;
			}
			ahct.testAddHostCate(ahct.json_str(seg_arr[0], seg_arr[1]));
		}
	}
}
