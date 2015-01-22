package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import cn.clickwise.lib.string.SSO;

public class FilterFile {

	public static void main(String[] args) throws Exception
	{
		
		BufferedReader br=new BufferedReader(new FileReader("D:/projects/medlda_win_workplace/medlda/tbsample/crawl_tb/5/ppsj_0120.txt"));
		PrintWriter pw=new PrintWriter(new FileWriter("D:/projects/medlda_win_workplace/medlda/tbsample/crawl_tb/5/ppsj_0120_1.txt"));
		
		String line="";
		while((line=br.readLine())!=null)
		{
			if(SSO.tioe(line))
			{
				continue;
			}
			line=line.trim();
			
		    if((line.indexOf("老人")>-1)||(line.indexOf("老年人")>-1)||(line.indexOf("苹果")>-1)||(line.toLowerCase().indexOf("iphone")>-1)||(line.indexOf("音乐")>-1)||(line.indexOf("电源")>-1)||(line.indexOf("贴膜")>-1)||(line.indexOf("耳机")>-1)||(line.indexOf("飞利浦")>-1)||(line.indexOf("汽车")>-1))
		    {
				continue;
			}
		    
		    if(line.length()<40)
		    {
		    	continue;
		    }
			
			pw.println(line);
		}
		
		
		br.close();
		pw.close();
		
	}
}
