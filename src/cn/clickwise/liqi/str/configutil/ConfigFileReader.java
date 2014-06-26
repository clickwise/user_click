package cn.clickwise.liqi.str.configutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import cn.clickwise.liqi.str.basic.SSO;

/**
 * 从普通文件建立Properties对象
 * @author lq
 *
 */

public class ConfigFileReader {

	/**
	 * 从普通文件建立Properties对象
	 * @param file_name
	 * @return prop
	 * @throws Exception
	 */
	public static Properties getPropertiesFromFile(String file_name) throws Exception
	{
		Properties prop=new Properties();
		FileReader fr=new FileReader(new File(file_name));
		BufferedReader br=new BufferedReader(fr);
		String line="";
		String[] seg_arr=null;
		String pkey="";
		String pval="";
		//System.out.println("read the config file");
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			//System.out.println("prop line:"+line);
			if(!(SSO.tnoe(line)))
			{
				continue;
			}
			if(line.startsWith("#"))
			{
				continue;
			}
			if(line.indexOf("#")>0)
			{
			  line=line.substring(0,line.indexOf("#"));
			}
			seg_arr=line.split("=");
			if(seg_arr.length!=2)
			{
				continue;
			}
		    pkey=seg_arr[0].trim();
		    pval=seg_arr[1].trim();
		    
			if(!(SSO.tnoe(pkey)))
			{
				continue;
			}
			if(!(SSO.tnoe(pval)))
			{
				continue;
			}
			System.out.println(pkey+" "+pval);
			prop.setProperty(pkey, pval);
		}
		
		return prop;
	}
	
}
