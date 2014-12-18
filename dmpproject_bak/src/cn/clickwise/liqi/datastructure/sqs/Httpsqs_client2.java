package cn.clickwise.liqi.datastructure.sqs;

import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.hbase.util.Base64;

import cn.clickwise.liqi.time.utils.TimeOpera;

import redis.clients.jedis.Jedis;

public class Httpsqs_client2 {

	public static void main(String[] args) throws Exception
	{
		Httpsqs_client sqs_fetch=new Httpsqs_client("42.62.29.24","1218","utf-8");
		Jedis jedis = new Jedis("42.62.29.24", 16379, 1000);// redis服务器地址	
		jedis.select(14);
		String one_item="";
		int index=0;
		while((one_item.indexOf("END")==-1)&&(index<3))
		{
		   
			one_item=sqs_fetch.get("tb_msg");
			System.out.println("one_item:"+one_item);
			if(one_item.indexOf("tbi")>-1)
			{
				index++;
			}
			else
			{
				index++;
				//continue;
			}
			//Set<String> js=jedis.zrangeByScore("tbi:209d12f56f2442871fee3fe34ce2294c",(long) ((double)TimeOpera.str2long("2014-02-08 15:29:05")/(double)1000), (long)((double)(TimeOpera.getCurrentTimeLong()+100000)/(double)1000));
			Set<String> js=jedis.zrangeByScore(one_item,(long) ((double)TimeOpera.str2long("2014-02-08 15:29:05")/(double)1000), (long)((double)(TimeOpera.getCurrentTimeLong()+100000)/(double)1000));
			System.out.println(js.size());
			Iterator js_it=js.iterator();
			while(js_it.hasNext())
			{
				System.out.println(js_it.next());
			}
			
			/*
			if(((one_item.indexOf("add_tbi"))>-1)||((one_item.indexOf("add_tbi"))>-1))
			{
			  index++;
			  System.out.println("one_item:"+one_item);
			  System.out.println("one_item:"+new String(Base64.decode(one_item.replace("add_tbi?s=", ""))));
			}
			*/
		}
		
	}
}
