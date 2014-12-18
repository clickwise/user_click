package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.clickwise.liqi.str.basic.SSO;


public class PortCname {

	
	
	public static void main(String[] args) throws Exception
	{
	  FileReader fr=new FileReader(new File("E:/eclipse_java_workspace/SWAProject/input/stdout"));
	  BufferedReader br=new BufferedReader(fr);
	  String line="";
	  FileWriter fw=new FileWriter(new File("E:/eclipse_java_workspace/SWAProject/output/dns/port_cname.txt"));
	  PrintWriter pw=new PrintWriter(fw);
      String eregex="[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.([a-zA-Z0-9_\\-\\.]*?)\\s*>\\s*[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.([a-zA-Z0-9_\\-\\.]*?)\\s*:";
	  Pattern epat=Pattern.compile(eregex);
	  Matcher emat=null;
	  String fmat="";
	  String smat="";
	  Hashtable<String,String> pc_hash=new Hashtable<String,String>();
      while((line=br.readLine())!=null)
	  {
		  line=line.trim();
		//.out.println("line:"+line);
          emat=epat.matcher(line);
          if(emat.find())
          {
        	  fmat=emat.group(1);
        	  smat=emat.group(2);
        	  System.out.println("fmat:"+fmat+" smat:"+smat); 
              if(!(SSO.tnoe(fmat)))
              {
            	  continue;
              }
              if(!(SSO.tnoe(smat)))
              {
            	  continue;
              }
              
              fmat=fmat.trim();
              smat=smat.trim();
              if(!(pc_hash.containsKey(fmat)))
              {
            	  pc_hash.put(fmat, fmat);
              }
              if(!(pc_hash.containsKey(smat)))
              {
            	  pc_hash.put(smat, smat);
              }
          }
	  }
      
      Enumeration pc_enum=pc_hash.keys();
      String pc_temp="";
      while(pc_enum.hasMoreElements())
      {
    	  pc_temp=pc_enum.nextElement()+"";
    	  pw.println(pc_temp);
      }
	  fw.close();
	  pw.close();
	  br.close();
	  fr.close();
	}
	
	
}
