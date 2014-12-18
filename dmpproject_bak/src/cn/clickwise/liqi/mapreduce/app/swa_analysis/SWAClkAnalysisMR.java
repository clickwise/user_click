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



public class SWAClkAnalysisMR {

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
						  word.set(wd);
						  word1.set(input_url);
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
            
            Hashtable<String,Integer> url_hash=new Hashtable<String,Integer>();
            int old_count=0;
            
			while(it.hasNext()) {
				val=it.next().toString();
				val=val.trim();
				if((val==null)||(val.equals("")))
				{
					continue;
				}
				url=val;
				if(!(url_hash.containsKey(url)))
				{
					url_hash.put(url, 1);
				}
				else
				{
					old_count=url_hash.get(url);
					old_count=old_count+1;
					url_hash.remove(url);
					url_hash.put(url, old_count);
				}
			}
			
			Enumeration url_enum=url_hash.keys();
			String temp_url="";
			int temp_count=0;
			SortElement sortele;
			ArrayList<SortElement> al = new ArrayList<SortElement>();
			while(url_enum.hasMoreElements())
			{
				temp_url=url_enum.nextElement()+"";
				temp_count=url_hash.get(temp_url);
            	sortele=new SortElement(temp_url,temp_count);
            	al.add(sortele);				
			}
			Collections.sort(al);
			
			Iterator it_url = al.iterator();
			SortElement tst=null;
			String url_info="";
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
				url_info=url_info+temp_url+" "+temp_count+"\001";           
			}
			if(url_info==null)
			{
				url_info="";
			}
			url_info=url_info.trim();
			result.set(url_info);
			context.write(key, result);

		}
		
		public class SortElement implements Comparable{
			
			public String key;
			public double val;
			
			public SortElement(String key,double val)
			{
			   this.key=key;
			   this.val=val;

			}
			public int compareTo(Object o) {
				SortElement s = (SortElement)o;
				return val < s.val ? 1 : (val == s.val ? 0 : -1);
			};
			
			public String toString(){
			    return  "key = " + this.key+ ",val = " + this.val;
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
			System.err.println("Usage: SWAClkAnalysisMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "SWAClkAnalysisMR_" + day);
		job.setJarByClass(SWAClkAnalysisMR.class);
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
