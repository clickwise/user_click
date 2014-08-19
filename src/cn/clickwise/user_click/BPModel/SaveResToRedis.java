package cn.clickwise.user_click.BPModel;

import java.io.File;

import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.str.basic.SSO;

import redis.clients.jedis.Jedis;

public class SaveResToRedis {

	public Jedis jedis;
	public String host_prefix="hostmatch_";
	public void init()
	{
		jedis = new Jedis("106.187.35.172", 16379, 1000);// redis服务器地址
		jedis.select(14);
	}
	public void save(File resFile)
	{
		try{
		String[] recs=FileToArray.fileToDimArr(resFile);
		String match="";
		String host="";
		
		String adid="";
		String sim="";
		String[] seg=null;
		for(int i=0;i<recs.length;i++)
		{
			match=recs[i];
			if(SSO.tioe(match))
			{
				continue;
			}
			match=match.trim();
			seg=match.split("\001");
			if(seg.length!=3)
			{
				continue;
			}
			host=seg[0].trim();
		    adid=seg[1].trim();
			sim=seg[2].trim();
			System.out.println(host_prefix+host+":"+adid+"\001"+sim);
			jedis.set(host_prefix+host, adid+"\001"+sim);	
		}
		
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static void main(String[] args)
	{
		SaveResToRedis srt=new SaveResToRedis();
		srt.init();
		srt.save(new File("temp/sample/res2.txt"));
	}
	
}
