package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;



public class SWAClkAnalysisTitleMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] seg_arr = (value.toString()).split("\t");

			String hourseg="";
            String input_ip="";
            String input_area="";
            String input_cookie="";
            String host="";
            String input_url="";
            String input_title="";
            String title_fenci="";
            String input_ci_ip="";
            String input_ci_ip_area="";
            String input_refer="";
            String wd="";
            				            
			if (seg_arr != null && seg_arr.length == 11) {
	
                hourseg=seg_arr[0].trim();
                input_ip=seg_arr[1].trim();
                input_area=seg_arr[2].trim();
                input_cookie=seg_arr[3].trim();
                host=seg_arr[4].trim();
                input_url=seg_arr[5].trim();
                input_title=seg_arr[6].trim();
                title_fenci=seg_arr[7].trim();
                input_ci_ip=seg_arr[8].trim();
                input_ci_ip_area=seg_arr[9].trim();
                input_refer=seg_arr[10].trim();
                
				if ((input_refer != null) && (!input_refer.equals(""))) {		
					if(isBaiduSearch(input_refer))
					{
						wd=extract_word(input_refer);
						if((wd!=null)&&(!((wd.trim()).equals(""))))
						{
						  //System.out.println(wd+"  "+input_url);
						  if(input_title==null)
						  {
							 input_title="";
						  }
						  input_title.replaceAll("\t", " ");
						  word.set(wd);
						  word1.set(input_url+"\t"+input_title);
						  context.write(word, word1);
						  
						}
					}
				}
			}
		}

		public boolean isValidTitle(String title) {
			title = title.trim();
			if(title==null||title.equals(""))
			{
				return false;
			}
			boolean isVal = true;
			String eng_mat = "[a-zA-Z0-9\\.:\\?#=_/&\\-%]*";
			if (Pattern.matches(eng_mat, title)) {
				isVal = false;
			}
			if ((title.indexOf("<") != -1) || (title.indexOf(">") != -1)) {
				isVal = false;
			}

			char first_char = title.charAt(0);
			if (Pattern.matches(eng_mat, first_char+"")) {
				isVal = false;
			}
			if(!isChinese(first_char))
			{
				isVal = false;
			}

			return isVal;
		}

		public  boolean isChinese(char a) {
			int v = (int) a;
			return (v >= 19968 && v <= 171941);
		}
		
		public boolean isBaiduSearch(String refer)
		{
			boolean isbs=false;
	        String bd_reg="http:\\/\\/www.baidu.com\\/s\\?.*?wd=.*?";
	        //Pattern bd_pat=Pattern.compile(bd_reg);
	        //Matcher bd_mat=bd_pat.matcher(bd_reg);
	        isbs=Pattern.matches(bd_reg, refer);     
			return isbs;
		}
		
		public String extract_word(String refer)
		{
			String wd="";
	        String bd_reg="http:\\/\\/www.baidu.com\\/s\\?.*?wd\\=(.*?)\\&";
	        Pattern bd_pat=Pattern.compile(bd_reg);
	        Matcher bd_mat=bd_pat.matcher(refer);
	        while(bd_mat.find())
	        {
	        	wd=bd_mat.group(1);
	        }
	        
	        if((wd==null)||((wd.trim()).equals("")))
	        {
	        	bd_reg="http:\\/\\/www.baidu.com\\/s\\?.*?wd=(.*?)$";
	            bd_pat=Pattern.compile(bd_reg);
	            bd_mat=bd_pat.matcher(refer);
	            while(bd_mat.find())
	            {
	            	wd=bd_mat.group(1);
	            }
	        }
	        //System.out.println("wd:"+wd);
			if(wd==null)
			{
				wd="";
			}
			else
			{
				try{
				wd=URLDecoder.decode(wd);
				}
				catch(Exception e)
				{
					
				}
			}  
			
			if(wd==null)
			{
				wd="";
			}
			return wd;
		}
		
	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Text> it = values.iterator();
            String val="";
            String url="";
            
            Hashtable<String,String> url_hash=new Hashtable<String,String>();

            
            String[] seg_arr=null;
            String title="";
            String old_title_count="";
            String old_title="";
            String old_count_str="";
            int old_count=0;
            
            String[] old_seg_arr=null;
            
			while(it.hasNext()) {
				val=it.next().toString();
				val=val.trim();
				if((val==null)||(val.equals("")))
				{
					continue;
				}
				seg_arr=val.split("\t");
				if(seg_arr.length<1)
				{
					continue;
				}
				url=seg_arr[0].trim();
				title="";
				if(seg_arr.length>1)
				{
					title=seg_arr[1].trim();
				}
				
				if((url==null)||(url.equals("")))
				{
					continue;
				}
				
				if(!(url_hash.containsKey(url)))
				{
					url_hash.put(url, "1\t"+title);
				}
				else
				{
					old_title_count=url_hash.get(url);
					old_seg_arr=old_title_count.split("\t");
				    old_title="";
				    old_count_str="";
				    if(old_seg_arr.length>0)
				    {
				    	old_count_str=old_seg_arr[0].trim();
				    	old_count=Integer.parseInt(old_count_str);
				    	old_count++;				    	
				    }
				    if(old_seg_arr.length>1)
				    {
				    	old_title=old_seg_arr[1].trim();
				    	if(old_title.length()>title.length())
				    	{
				    		old_title=title;
				    	}
				    }
				    
					url_hash.remove(url);
					url_hash.put(url, old_count+"\t"+old_title);
				}
			}
			
			Enumeration url_enum=url_hash.keys();
			String temp_url="";
			int temp_count=0;
			SortElement sortele;
			ArrayList<SortElement> al = new ArrayList<SortElement>();
			String temp_count_title="";
			String[] tct_seg=null;
			String temp_title="";
			while(url_enum.hasMoreElements())
			{
				temp_url=url_enum.nextElement()+"";
				temp_count_title=url_hash.get(temp_url);
				tct_seg=temp_count_title.split("\t");
				temp_title="";
				if(tct_seg.length<1)
				{
					continue;
				}
				temp_count=0;
				temp_count=Integer.parseInt(tct_seg[0]);
				if(tct_seg.length>1)
				{
					temp_title=tct_seg[1];
				}
				
            	sortele=new SortElement(temp_url,temp_count,temp_title);
            	al.add(sortele);				
			}
			
			Collections.sort(al);
			
			Iterator it_url = al.iterator();
			SortElement tst=null;
			String url_info="\001";
			while(it_url.hasNext()){
				tst=(SortElement)it_url.next();
                if(tst==null)
                {
                	continue;
                }
                temp_url=tst.key;
                temp_count=(int)tst.val;
				if((temp_url==null)||((temp_url.trim()).equals(""))||(temp_count<1))
				{
					continue;
				}     
				url_info=url_info+temp_count+"\t"+temp_url+"\t"+temp_title+"\001";           
			}
			if(url_info==null)
			{
				url_info="";
			}
			//url_info=url_info.trim();
			result.set(url_info);
			context.write(key, result);

		}
		
		public class SortElement implements Comparable{
			
			public String key;
			public double val;
			public String add_info;
			
			public SortElement(String key,double val,String add_info)
			{
			   this.key=key;
			   this.val=val;
               this.add_info=add_info;
			}
			public int compareTo(Object o) {
				SortElement s = (SortElement)o;
				return val < s.val ? 1 : (val == s.val ? 0 : -1);
			};
			
			public String toString(){
			    return  "key = " + this.key+ ",val = " + this.val+",add_info = " + this.add_info;
			}		
		}
		
		public boolean isValidTitle(String title) {
			title = title.trim();
			if(title==null||title.equals(""))
			{
				return false;
			}
			boolean isVal = true;
			String eng_mat = "[a-zA-Z0-9\\.:\\?#=_/&\\-%]*";
			if (Pattern.matches(eng_mat, title)) {
				isVal = false;
			}
			if ((title.indexOf("<") != -1) || (title.indexOf(">") != -1)) {
				isVal = false;
			}

			char first_char = title.charAt(0);
			if (Pattern.matches(eng_mat, first_char+"")) {
				isVal = false;
			}
			if(!isChinese(first_char))
			{
				isVal = false;
			}

			return isVal;

		}

		public  boolean isChinese(char a) {
			int v = (int) a;
			return (v >= 19968 && v <= 171941);
		}		
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: SWAClkAnalysisTitleMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "SWAClkAnalysisTitleMR_" + day);
		job.setJarByClass(SWAClkAnalysisTitleMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(100);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}	
}
