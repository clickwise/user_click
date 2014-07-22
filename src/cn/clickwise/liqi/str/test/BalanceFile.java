package cn.clickwise.liqi.str.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import love.cq.util.MapCount;
import cn.clickwise.liqi.file.uitls.FieldCount;
import cn.clickwise.liqi.str.basic.SSO;

public class BalanceFile {
	
	public void balance(File input_file,int field_num,int index,int text_index,File output_file)
	{
		
		FileInputStream fis = null;
		InputStreamReader isr = null;

		BufferedReader br = null;
		String record="";
		String[] seg_arr=null;
		String field="";

		
		FileWriter fw=null;
		PrintWriter pw=null;
		
		HashMap<String,Integer> mm=new HashMap<String,Integer>();
		int oldc=0;
		String text="";
		String ntext="";
		String label="";
		try {
			// fr=new FileReader(input_file);
			fis = new FileInputStream(input_file.getAbsolutePath());
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			fw=new FileWriter(output_file);
			pw=new PrintWriter(fw);
			while((record=br.readLine())!=null)
			{
			  if(SSO.tioe(record))
			  {
				  continue;
			  }
			  record=record.trim();
			  //System.out.println("record:"+record);
			  seg_arr=record.split("\001");
			  if(seg_arr==null)
			  {
				  continue;
			  }
			  if(seg_arr.length!=field_num)
			  {
				  continue;
			  }
			  text=seg_arr[text_index];
			  if(SSO.tioe(text))
			  {
				  continue;
			  }
			  
			  ntext=filter(text);
			  if(!(isPick(ntext)))
			  {
				  continue;
			  }
			  field=seg_arr[index];
			 // System.out.println("field:"+field);
			  if(SSO.tioe(field))
			  {
				  continue;
			  }
			  field=field.trim();
			  
			  if(!(mm.containsKey(field)))
			  {
				  mm.put(field, 1);
				  pw.println(field+"\001"+ntext);
			  }
			  else
			  {
				  oldc=mm.get(field);
				  if(oldc<1000)
				  {
					  pw.println(field+"\001"+ntext);
				  }
				  mm.remove(field);
				  mm.put(field, oldc+1);
				  
			  }
			  	
			}
			
			br.close();
            fw.close();
            pw.close();
			/*
		    Set s=mm.keySet();
		    Iterator<String> it=s.iterator();
		    String key="";
		    while(it.hasNext())
		    {
		    	key=it.next();
		    	System.out.println(key+":"+mm.get(key));
		    }
			*/
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public boolean isPick(String text)
	{
		String[] seg_arr=text.split("\\s+");
		String item="";
		int nc=0;
		int mc=0;
		for(int i=0;i<seg_arr.length;i++)
		{
			item=seg_arr[i];
			mc++;
			if(!(item.equals("NA")))
			{
				nc++;
			}
		}
		if(nc<=3||mc<=7)
		{
			return false;
		}
		return true;
	}
	
	public String filter(String text)
	{
		String[] seg_arr=text.split("\\s+");
		String nt="";
		String item="";
		for(int i=0;i<seg_arr.length;i++)
		{
			item=seg_arr[i];
			if((item.equals("天猫"))||(item.equals("淘宝"))||(item.equals("包邮"))||(item.equals("正品"))||(item.equals("\\/")))
			{
				item="NA";
			}
			nt=nt+item+" ";
		}
		nt=nt.trim();
		
		return nt;
	}
	public static void main(String[] args)
	{
		if(args.length!=5)
		{
			System.err.println("Usage:<input_file> <field_num> <key_index> <text_index> <output_file>");
			System.exit(1);
		}
		
		File input=new File(args[0]);
		File output=new File(args[4]);
		BalanceFile fc=new BalanceFile();
		fc.balance(input, Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]),output);
	
	}
}
