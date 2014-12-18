package cn.clickwise.liqi.test.str;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import cn.clickwise.liqi.office.excel.create.CreateExcelFile;
import cn.clickwise.liqi.str.basic.FileToArray;
import cn.clickwise.liqi.str.basic.SSO;

public class UrlAddTitle {

	public void addTitle(String statis_file,String title_dir,String output_file) throws Exception
	{
		HashMap<String,String> title_hash=new HashMap<String,String>();
		File td=new File(title_dir);
		File[] title_files=td.listFiles();
		String url="";
		String title="";
		for(int i=0;i<title_files.length;i++)
		{
			System.out.println(title_files[i].getAbsolutePath());
			String[][] da=FileToArray.fileToDoubleDimArr(title_files[i].getAbsolutePath(), "\001");
			if(da==null)
			{
				continue;
			}
			for(int j=0;j<da.length;j++)
			{
			if(da[j].length!=3)
			{
				continue;
			}
			
			url=da[j][1].trim();
			title=da[j][2].trim();
			if(!(title_hash.containsKey(url)))
			{
				if(SSO.tnoe(title))
				{
					title_hash.put(url, title);
				}
			}
			}
			
		}
		
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);
		String[][] ta=FileToArray.fileToDoubleDimArr(statis_file, "\001");
		String num="";
		for(int i=0;i<ta.length;i++)
		{
			if(ta[i].length!=2)
			{
				continue;
			}
			url=ta[i][0].trim();
			num=ta[i][1].trim();
			if(SSO.tnoe(url))
			{
				title=title_hash.get(url);
				if(!(SSO.tnoe(title)))
				{
					title="";
				}
				pw.println(url+"\001"+title+"\001"+num);
			}
		}
		
		pw.close();
		fw.close();
		
		
		
	}
	
	public static void main(String[] args) throws Exception
	{
		String statis_file="D:/projects/urls/xd/20140318/xd_url/000000_0";
		String title_dir="D:/projects/urls/xd/20140318/xd";
		String output_file="D:/projects/urls/xd/20140318/xd_url_num.txt";
		
		UrlAddTitle uat=new UrlAddTitle();
		uat.addTitle(statis_file, title_dir, output_file);
		String excel_file="D:/projects/urls/xd/20140318/xd_excel.xls";
		String[] txtfiles={"D:/projects/urls/xd/20140318/xd_url_num.txt"};
		CreateExcelFile.txtsToExcelFile(txtfiles, excel_file);
	}
	
}
