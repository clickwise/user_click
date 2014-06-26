package cn.clickwise.redis;

import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.str.edcode.Base64Code;
import redis.clients.jedis.Jedis;

public class WriteDictToRedis {

	public static void main(String[] args) throws Exception
	{
		Jedis jedis = new Jedis("42.62.29.25", 16379, 10000);// redis服务器地址	
		jedis.select(2);
		jedis.ping();
		System.out.println("dbsize:"+jedis.dbSize());
		//System.exit(1);
		String[] dicts=FileToArray.dirToDimArr("D:/projects/admatch/dict");
		String word="";
		for(int i=0;(i<dicts.length);i++)
		{
			word=dicts[i];
			word=word.trim();
			String codeword=Base64Code.getEncodeStr(word);
			if(!(jedis.equals(codeword)))
			{
			  jedis.set(codeword, "1");
			}
			if((i%1000)==1)
			{
				System.out.println("i="+i+" word:"+word+" encodeword:"+codeword);
			}
		}
	}
	
	
}
