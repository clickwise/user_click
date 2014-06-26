package cn.clickwise.smartjobs;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.edcode.UrlCode;
import cn.clickwise.liqi.time.utils.TimeOpera;
import cn.clickwise.segmenter.stanford.StanterSeg;

/**
 * 提取host,url,refer_host,refer_url,link_keyword,time,uid
 * @author zkyz
 */
public class NstatUserAnalysis {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day = "";
		public StanterSeg ss;
		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();		
			ss=new StanterSeg();
			Properties prop=new Properties();
			prop.setProperty("seg_server", "192.168.110.186");
			prop.setProperty("seg_port", "8092");
			prop.setProperty("use_seg_server", "true");
			prop.setProperty("swa_dict_ip", "192.168.110.182");
			prop.setProperty("swa_dict_port", "6379");
			prop.setProperty("swa_dict_db", "2");
			ss.load_config(prop);
			
		}
		
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String nstat_line = value.toString().trim();
			String area = "";
			String atime = "";
			String pname = "";
			String sip = "";
			String dip = "";
			String host = "";//
			String url = "";//
			String refer = "";
			String cookie = "";
			String loc = "";
			String[] seg_arr = null;
			seg_arr = nstat_line.split("\001");
			String info = "";

			String[] urlwords=null;
			String[] referwords=null;
			String[] mergewords=null;
			
			String refer_host="";
			String refer_url="";
			String[] split_refer=null;
			String link_word="";
			long ltime=0;
			String only_cookie="";
			
			if (seg_arr.length == 10) {
				area = seg_arr[0].trim();
				atime = seg_arr[1].trim();
				pname = seg_arr[2].trim();
				sip = seg_arr[3].trim();
				dip = seg_arr[4].trim();
				host = seg_arr[5].trim();
				url = seg_arr[6].trim();
				refer = seg_arr[7].trim();
				cookie = seg_arr[8].trim();
				loc = seg_arr[9].trim();
				urlwords=wordFromLink(url);
				referwords=wordFromLink(refer);
				mergewords=merge_arr(urlwords,referwords);
				ltime=TimeOpera.str2long(atime);
				only_cookie=cookie_from_str(cookie);
				
				split_refer=refer_split(refer);
				refer_host=split_refer[0].trim();
				refer_url=split_refer[1].trim();
				link_word=arrToStr(mergewords);
				
				if ((SSO.tnoe(cookie))&&((cookie.indexOf("NA"))==-1)&&(SSO.tnoe(host))&&(SSO.tnoe(only_cookie))&&((host.indexOf("null"))==-1)) {              
					word.set(host);
					info=url+"\001"+refer_host+"\001"+refer_url+"\001"+link_word+"\001"+ltime+"\001"+only_cookie;
					word1.set(info);
					context.write(word, word1);
				}
				
				/*
				if ((SSO.tnoe(cookie))&&((cookie.indexOf("NA"))==-1)) {              
					word.set(area);
					info=atime+"\001"+pname+"\001"+sip+"\001"+dip+"\001"+host+"\001"+url+"\001"+refer+"\001"+cookie+"\001"+loc;
					word1.set(info);
					context.write(word, word1);
				}
				*/
			}//seg_arr.length
		}//map
		
		
		public String[] wordFromLink(String link)
		{
			String urlcode_pat_str="([A-F\\+\\%0-9]*)";
			Pattern urlcode_pat=Pattern.compile(urlcode_pat_str);
			Matcher urlcode_match=urlcode_pat.matcher(link);
			Vector<String> words=new Vector();
			String decodeword="";
			String encodestr="";
			String seg_str=null;
			String[] seg_arr=null;
			String one_word="";
			try{
			  while(urlcode_match.find())
			  {
				encodestr=urlcode_match.group(1);
				decodeword=UrlCode.getDecodeUrl(encodestr);
				if(SSO.tioe(decodeword))
				{
					continue;
				}
				char first_char = decodeword.charAt(0);
				if(isChinese(first_char))
				{
					seg_str=ss.seg_inte(decodeword);
					seg_arr=seg_str.split("\\s+");
					for(int i=0;i<seg_arr.length;i++)
					{
						one_word=seg_arr[i];
						if(SSO.tioe(one_word))
						{
							continue;
						}
						one_word=one_word.trim();
						words.add(one_word);
					}
				}
				else
				{
					continue;
				}
			  }
			}
			catch(Exception e)
			{
				
			}
			
			String[] arr=new String[words.size()];
			for(int i=0;i<arr.length;i++)
			{
				arr[i]=words.get(i);
			}		
			return arr;
		}
		
		public String[] merge_arr(String[] arr1,String[] arr2)
		{
			String[] arr3=new String[arr1.length+arr2.length];
			int index=0;
			for(int i=0;i<arr1.length;i++)
			{
				arr3[index++]=arr1[i];
			}
			
			for(int i=0;i<arr2.length;i++)
			{
				arr3[index++]=arr2[i];
			}
			return arr3;
		}
		
		public String[] refer_split(String refer)
		{
			String[] split_arr=null;
			split_arr=new String[2];
			
			String host="";
			String url="";
			
			refer=refer.replaceFirst("http://", "");
			host=SSO.beforeStr(refer, "/");
			url=SSO.afterStr(refer, "/");
			
			split_arr[0]=host;
			split_arr[1]=url;
			
			return split_arr;
		}
		
		public boolean isChinese(char a) {
			int v = (int) a;
			return (v >= 19968 && v <= 171941);
		}

		public String arrToStr(String[] arr)
		{
			String arr_str="";
			String word="";
			for(int i=0;i<arr.length;i++)
			{
				word=arr[i];
				if(SSO.tioe(word))
				{
					continue;
				}
				word=word.trim();
				arr_str=arr_str+word+" ";
			}
			arr_str=arr_str.trim();
			return arr_str;
		}
		
		public String cookie_from_str(String cook_str)
		{
			String cookie="";
			String cpat_str="uid=([0-9a-zA-Z]*)";
			
			Pattern pat=Pattern.compile(cpat_str);
			Matcher mat=pat.matcher(cook_str);
			if(mat.find())
			{
				cookie=mat.group(1);
			}
			cookie=cookie.trim();
			
			return cookie;
		}
		
	}//PrepareMapper

	private static class PrepareReducer extends
			Reducer<Text, Text, Text, NullWritable> {
		private Text result = new Text();
		private Text result_key = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			String key_str = key.toString().trim();
			Iterator<Text> it = values.iterator();
			String info = "";
			if (SSO.tnoe(key_str)) {
				while (it.hasNext()) {
					info = it.next().toString();
					info = info.trim();
					if (SSO.tnoe(info)) {
						result_key.set(key_str + "\001" + info);
						context.write(result_key, NullWritable.get());
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: NstatUserAnalysis <day> <input> <output>");
			System.exit(2);
		}

		String day = otherArgs[0];
		Job job = new Job(conf, "NstatUserAnalysis_" + day);
		job.setJarByClass(NstatUserAnalysis.class);
		PrepareMapper.request_day = day;
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(1);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
	
}
