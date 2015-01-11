package cn.clickwise.clickad.sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import cn.clickwise.lib.string.SSO;

public class SampleFormat {

	public static String lineFormat(String line) {
	    String cate="";
	    cate=line.substring(0,1);
	    //System.out.println("cate:"+cate);
	    String info=line.substring(2,line.length());
	    
	    String[] fields=info.split("\\|\\|\\|");
	    //System.out.println("fields.length:"+fields.length);
	    if(fields.length!=4)
	    {
	    	return "";
	    }
	    
	    String field="";
	    ArrayList<String> titles=new ArrayList<String>();
	    
	    String jdinfo=fields[0];
	    String bdinfo=fields[2];
	    String hostinfo=fields[3];
	    
	    //System.out.println("jdinfo:"+jdinfo);
	    //System.out.println("bdinfo:"+bdinfo);
	    //System.out.println("hostinfo:"+hostinfo);
	    fields=jdinfo.split("\\|\\|");
	    String title="";
	    for(int i=0;i<fields.length;i++)
	    {
	    	field=fields[i];
	    	if(SSO.tioe(field))
	    	{
	    		continue;
	    	}
	    	title=SSO.midstrs(field, "|jd|", "|NA|");
	    	//System.out.println("title:"+title);
	    	if(SSO.tioe(title))
	    	{
	    		continue;
	    	}
	    	title=title.replaceAll("\\|", " ").replaceAll("京东", " ");
	    	titles.add(title);
	    	
	    }
	    
	    fields=bdinfo.split("\\|\\|");
	    String[] subfields=null;
	    for(int i=0;i<fields.length;i++)
	    {
	    	field=fields[i];
	        subfields=field.split("\\|");
	        if(subfields.length!=3)
	        {
	        	continue;
	        }
	       // System.out.println("bdword:"+subfields[2]);
	        titles.add(subfields[2]);
	    }
	    
	    fields=hostinfo.split("\\|\\|");

	    String words="";
	    for(int i=0;i<fields.length;i++)
	    {
	    	field=fields[i];
	        subfields=field.split("\\|");
	        if(subfields.length<4)
	        {
	        	continue;
	        }
	    	
	        titles.add(subfields[0]);
	        titles.add(subfields[1]);
	        words=subfields[3];
	        if(SSO.tioe(words))
	        {
	        	continue;
	        }
	        
	        words=words.replaceAll(":\\d+", "");
	       // System.out.println("words:"+words);
	        titles.add(words);
	    } 
	    
	    String titleall="";
	    if((titles.size())<1)
	    {
	    	return "";
	    }
	    for(int i=0;i<titles.size()-1;i++)
	    {
	    	titleall=titleall+titles.get(i)+" ";
	    }
	    titleall=titleall.trim();
	   
		return cate+"\001"+titleall;
	}

	public static void main(String[] args) {
		try {
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(isr);

			OutputStreamWriter osw = new OutputStreamWriter(System.out);
			PrintWriter pw = new PrintWriter(osw);


			String line = "";
	        String fline="";
			while ((line = br.readLine()) != null) {
				if (SSO.tioe(line)) {
					continue;
				}
				line = line.trim();

				fline=lineFormat(line);
				if(SSO.tioe(fline))
				{
					continue;
				}
				
				pw.println(fline);
			}

			isr.close();
			osw.close();
			br.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
