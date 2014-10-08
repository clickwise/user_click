package cn.clickwise.lib.file;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cn.clickwise.lib.string.SSO;

public class PropertiesUtil {

	// 在default path 和 jar包里寻找名称为name的配置文件
	// ，并转换为properties
	public static Properties file2properties(String name) {

		Properties prop = new Properties();
		InputStream propis = null;
		File local = new File(name);

		try {
			if (local.exists()) {
				propis = new FileInputStream(local);
			} else {
				propis = PropertiesUtil.class.getResourceAsStream("/" + name);
			}
			prop.load(propis);
			propis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return prop;
	}

	public static Properties[] getMultipleProperties(String name) {
		
		String content = "";
		String line = "";
		List<Properties> propList=null;
		try {
			File file = new File(name);
			BufferedReader br=null;
			
			if(file.exists())
			{
			  FileReader fr = new FileReader(file);
			  br=new BufferedReader(fr);
			}
			else
			{	
			  InputStream fileIs=PropertiesUtil.class.getResourceAsStream("/" + name);
			  InputStreamReader isr=new InputStreamReader(fileIs);
			  br=new BufferedReader(isr);
			}
					
			propList = new ArrayList<Properties>();
			
			while((line=br.readLine())!=null)
			{
				content+=(line+"\n");
			}
			
			//fr.close();
			br.close();
			
			content=content.trim();
			
			String[] paragraphs=content.split("[#]+");
			String paragraph="";
			
			for(int j=0;j<paragraphs.length;j++)
			{
				paragraph=paragraphs[j];
				if(SSO.tioe(paragraph))
				{
					continue;
				}
				
				InputStream  propStream= new  ByteArrayInputStream(paragraph.getBytes("UTF-8"));
				Properties prop=new Properties();
				prop.load(propStream);
				propList.add(prop);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Properties[] propArray=new Properties[propList.size()];
		for(int i=0;i<propArray.length;i++)
		{
			propArray[i]=propList.get(i);
		}
		
		return propArray;
	}

}
