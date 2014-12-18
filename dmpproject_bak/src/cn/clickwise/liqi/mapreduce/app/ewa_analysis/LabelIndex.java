package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class LabelIndex {

	public void to_label_index_file(String label_file,String fs_file,String st_file) throws Exception
	{
		FileReader fr=new FileReader(new File(label_file));
		BufferedReader br=new BufferedReader(fr);
		String line="";
		String[] seg_arr=null;
		
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
		    seg_arr=line.split("_");
		    if((seg_arr.length)!=3)
		    {
		    	continue;
		    }
		    
		    
		}
		
		
		
	}
	
	
	
	
}
