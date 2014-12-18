package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;


public class EWASWCollect {

	
	public static void main(String[] args) throws Exception
	{
		if (args.length != 2) {
			System.err.println("Usage: EWASWCollect <input_dir> <output_file>");
			System.exit(2);
		}
		
		String input_dir=args[0];
		String output_file=args[1];
		System.out.println("arg0:"+args[0]);
		System.out.println("arg1:"+args[1]);
		//System.exit(1);
		
		File uesk=new File(input_dir);
		File[] dt_dirs=uesk.listFiles();
		File dt_dir=null;
		
		File[] ec_host_dirs=null;
		File ec_host_dir=null;
		
		File[] sw_files=null;
		File sw_file=null;
		
		Hashtable sw_hash=new Hashtable();
		String line="";
		FileReader fr=null;
		BufferedReader br=null;
		
		String[] seg_arr=null;
		String keywords="";
		String extra_info="";
		String[] key_arr=null;
		String keyword="";
		for(int i=0;i<dt_dirs.length;i++)
		{
			dt_dir=dt_dirs[i];
			ec_host_dirs=dt_dir.listFiles();
			for(int j=0;j<ec_host_dirs.length;j++)
			{
				ec_host_dir=ec_host_dirs[j];
				sw_files=ec_host_dir.listFiles();
				for(int k=0;k<sw_files.length;k++)
				{
					sw_file=sw_files[k];
				//	System.out.println("sw_file:"+sw_file.getAbsolutePath());
					fr=new FileReader(sw_file);
					br=new BufferedReader(fr);
					while((line=br.readLine())!=null)
					{
						line=line.trim();
						if((line==null)||(line.equals("")))
						{
							continue;
						}
						seg_arr=line.split("\001");
						if((seg_arr.length)<2)
						{
							continue;
						}
						keywords=seg_arr[0].trim();
						extra_info=seg_arr[1].trim();
						if((keywords==null)||(keywords.equals("")))
						{
							continue;
						}
						key_arr=keywords.split("\\s+");
						if((key_arr.length)<2)
						{
							continue;
						}
						
						for(int m=0;m<key_arr.length;m++)
						{
							keyword=key_arr[m].trim();
							if((keyword==null)||(keyword.equals("")))
							{
								continue;
							}
							if(!(sw_hash.containsKey(keyword)))
							{
								sw_hash.put(keyword, 1);
							}
						}
					}
					
					fr.close();
					br.close();
					
				}
				
				
			}	
		}
		
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);
		
		Enumeration sw_enum=sw_hash.keys();
		
		while(sw_enum.hasMoreElements())
		{
			keyword=sw_enum.nextElement()+"";
			keyword=keyword.trim();
			if((keyword==null)||(keyword.equals("")))
			{
				continue;
			}
			if((keyword.length()>1)&&(keyword.length()<50))
			{
		    	pw.println(keyword);
			}
		}
		
		
		pw.close();
		fw.close();
		

		
	}

	
	
}
