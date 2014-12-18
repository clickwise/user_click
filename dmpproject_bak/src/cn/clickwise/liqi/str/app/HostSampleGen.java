package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import cn.clickwise.liqi.str.basic.FileToArray;
import cn.clickwise.liqi.str.basic.SSO;

public class HostSampleGen {

	public static void main(String[] args) throws Exception
	{
		String label_host_file="D:/project/sample_dir/host_one_hier/all_host.txt";
		
		String host_info_file="D:/project/sample_dir/host_one_hier/all_sam.txt";
		String label_info_file="D:/project/sample_dir/host_one_hier/label_info.txt";
		String[][] lh_arr=null;
		lh_arr=FileToArray.fileToDoubleDimArr(label_host_file, "\\s+");
		HashMap<String,String> hl_hash=new HashMap<String,String>();
		System.out.println("lh_arr.length:"+lh_arr.length);
		String label="";
		String host="";
		
		for(int i=0;i<lh_arr.length;i++)
		{
			if(lh_arr[i].length!=2)
			{
				continue;
			}
		    
			label=lh_arr[i][0].trim();
			host=lh_arr[i][1].trim();
			if(!(SSO.tnoe(host)))
			{
				continue;
			}
			
			host=host.trim();
			
			if(!(hl_hash.containsKey(host)))
			{
				hl_hash.put(host, label);
				System.out.println("host:"+host+" label:"+label);
			}	
		}
		
		
		FileReader fr=new FileReader(new File(host_info_file));
		BufferedReader br=new BufferedReader(fr);
		
		String line="";
		
		FileWriter fw=new FileWriter(new File(label_info_file));
		PrintWriter pw=new PrintWriter(fw);
		
		String[] seg_arr=null;
		String host_info="";
		int docid=1;
		while((line=br.readLine())!=null)
		{
		    if(!(SSO.tnoe(line)))
		    {
		    	continue;
		    }
		    
		    line=line.trim();
		    seg_arr=line.split("\\s+");
		    
		    if(seg_arr.length<2)
		    {
		    	continue;   	
		    }
		    
		    host=seg_arr[0].trim();
		    host_info="";
		    
		    for(int j=1;j<seg_arr.length;j++)
		    {
		    	host_info=host_info+seg_arr[j]+" ";
		    }
		    
		    host_info=host_info.trim();
		    label=hl_hash.get(host);
		    if(!(SSO.tnoe(label)))
		    {
		        continue;	
		    }
		    
		    pw.println(label+"\001"+docid+"\001"+host_info);
		    docid++;
		}
		
	
		fr.close();
		br.close();
		fw.close();
		pw.close();
	}
	
	
	
}
