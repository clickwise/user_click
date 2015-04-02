package cn.clickwise.lib.string;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FieldMap {

	public void fmap()
	{
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);
		
		String line="";
		String[] fields=null;
		
		try{
		
		  String field="";
		  
		  while((line=br.readLine())!=null)
		  {
			 fields=line.split(";");
			 if(fields==null||fields.length<1)
			 {
				 continue;
			 }
			 
			 for(int j=0;j<fields.length;j++)
			 {
				 field=fields[j];
				 if(SSO.tioe(field))
				 {
					 continue;
				 }
				 
				 pw.println(field);
			 }
		  }
		  
		  br.close();
		  pw.close();
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		FieldMap fm=new FieldMap();
		fm.fmap();
	}
	
}
