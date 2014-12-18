import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
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

import redis.clients.jedis.Jedis;


public class SWAWordInfoMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] seg_arr = (value.toString()).split("\001");
	        //new_line=url+"\001"+title+"\001"+seg_title+"\001"+sws+"\001"+ips;
			String url = "";
			String title = "";
			String seg_title="";
			String sws="";
			String ips = "";
			URI uri = null;
			String[] temp_seg = null;
			String host="";
			String temp_word="";
			if (seg_arr != null && seg_arr.length == 5) {
				url = seg_arr[0].trim();
                title=seg_arr[1].trim();
                seg_title=seg_arr[2].trim();
                sws=seg_arr[3].trim();
                ips=seg_arr[4].trim();
                
                if((url!=null)&&(!url.equals("")))
                {
                   try{
                	   uri = new URI(url);
                	   host=uri.getHost();               	   
                   }
                   catch(Exception e)
                   {
                	   
                   }
                                   	
                   if((sws!=null)&&(!sws.equals("")))
                   {
                	   temp_seg=sws.split("\\s+");
                	   
                	   if((temp_seg!=null)&&(temp_seg.length>0))
                	   {
                		   for(int j=0;j<temp_seg.length;j++)
                		   {
                			  temp_word=temp_seg[j].trim();
                			  if((temp_word!=null)&&(!temp_word.equals("")))
                			  {
                				word.set(temp_word);
                				word1.set(host+"\001"+ips);
                				context.write(word, word1);                				  
                			  }                			                   			                  			   
                		   }                		                  		                  		   
                	   }               	   
                   }
                	       	                	
                }//url                           
			}	
		}

		public boolean isValidTitle(String title) {
			title = title.trim();
			if (title == null || title.equals("")) {
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
			if (Pattern.matches(eng_mat, first_char + "")) {
				isVal = false;
			}
			if (!isChinese(first_char)) {
				isVal = false;
			}

			return isVal;

		}

		public boolean isChinese(char a) {
			int v = (int) a;
			return (v >= 19968 && v <= 171941);
		}



		public String clean_one_word(String word) {
			String new_word = word;
			// new_word=new_word.replaceAll("``", "");
			// new_word=new_word.replaceAll("''", "");
			new_word = new_word.replaceAll("&nbsp;", "");
			new_word = new_word.replaceAll("&nbsp", "");
			new_word = new_word.replaceAll("&ldquo;", "");
			new_word = new_word.replaceAll("&ldquo", "");
			new_word = new_word.replaceAll("&rdquo;", "");
			new_word = new_word.replaceAll("&rdquo", "");
			// new_word=new_word.replaceAll(";", "");
			new_word = new_word.replaceAll("&", "");
			new_word = new_word.replaceAll("VS", "");
			new_word = new_word.replaceAll("-RRB-", "");
			new_word = new_word.replaceAll("-LRB-", "");
			new_word = new_word.replaceAll("_", "");
			// new_word=new_word.replaceAll("[\\.]*", "");
			new_word = new_word.replaceAll("\\\\\\#", "");
			/*
			 * if (Pattern.matches("[a-zA-Z\\,\\.\\?0-9\\!\\-\\s]*", new_word))
			 * { return ""; } if (!(Pattern.matches("[\\u4e00-\\u9fa5]+",
			 * new_word))) { return ""; }
			 */
			new_word = new_word.replaceAll("★", "");
			new_word = new_word.replaceAll("__", "");
			new_word = new_word.replaceAll("ˇ", "");
			new_word = new_word.replaceAll("®", "");
			new_word = new_word.replaceAll("♣", "");
			new_word = new_word.replaceAll("\\丨", "");
			new_word = new_word.trim();

			return new_word;
		}

		

	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Text> it = values.iterator();
            String val="";
			String[] host_ips_seg=null;
            String host="";
            String ips="";
            int ips_val=0;
            int ips_old_val=0;
            int ips_new_val=0;
            Hashtable<String,Integer> host_ips_hash=new Hashtable<String,Integer>();
            
            int c=0;
			while(it.hasNext()) {
				//c++;
				//if(c>1000)
				//{
				//	break;
				//}
				val=it.next().toString();
				val=val.trim();
				
			    if((val==null)||(val.equals("")))
			    {
                   continue;			    	
			    }
				
				host_ips_seg=val.split("\001");
				if(host_ips_seg.length!=2)
				{
					continue;
				}
				
				host=host_ips_seg[0].trim();
				ips=host_ips_seg[1].trim();
				
				if((host==null)||(host.equals("")))
				{
					continue;
				}
				
				if((ips==null)||(ips.equals("")))
				{
					continue;
				}
				
				ips_val=Integer.parseInt(ips);
				if(!(host_ips_hash.containsKey(host)))
				{
					host_ips_hash.put(host, ips_val);
				}
				else
				{
					ips_old_val=host_ips_hash.get(host);
					host_ips_hash.remove(host);
					ips_new_val=ips_old_val+ips_val;
					host_ips_hash.put(host, ips_new_val);
				}
					
			}
			
			String ws="";
			Enumeration host_enum=host_ips_hash.keys();
			
			while(host_enum.hasMoreElements())
			{
				host=host_enum.nextElement()+"";
				if((host==null)||(host.trim().equals("")))
				{
					continue;
				}
			    ips_val=host_ips_hash.get(host);
			    if(ips_val<1)
			    {
			    	continue;
			    }
			    host=host.trim();
			    
				if((host==null)||(host.equals("")))
				{
					continue;
				}
				
			    ws=ws+host+" "+ips_val+"\001";			
			}
			ws=ws.trim();
			
			if(!(ws.equals("")))
			{
			    result.set(ws);
			   context.write(key, result);
			}
		}
	}
	
    public static class PrepareCombiner extends Reducer<Text,Text,Text,Text> {  
        
        // Reduce Method  
    	private Text result = new Text();
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {  
                   	
        	Iterator<Text> it = values.iterator();
        	Hashtable<String,Integer> host_ips_hash=new Hashtable<String,Integer>();
        	String val="";
        	String[] host_ips_seg=null;
            String host="";
            String ips="";
            int ips_val=0;
            int ips_old_val=0;
            int ips_new_val=0;
            int c=0;        	
        	while(it.hasNext()) {
        		//c++;
        		//if(c>1000)
        		//{
        		//	break;
        		//}
				val=it.next().toString();
				val=val.trim();
				
			    if((val==null)||(val.equals("")))
			    {
                   continue;			    	
			    }
				
				host_ips_seg=val.split("\001");
				if(host_ips_seg.length!=2)
				{
					continue;
				}
				
				host=host_ips_seg[0].trim();
				ips=host_ips_seg[1].trim();
				
				if((host==null)||(host.equals("")))
				{
					continue;
				}
				
				if((ips==null)||(ips.equals("")))
				{
					continue;
				}
				
				ips_val=Integer.parseInt(ips);
        		
				if(!(host_ips_hash.containsKey(host)))
				{
					host_ips_hash.put(host, ips_val);
				}
				else
				{
					ips_old_val=host_ips_hash.get(host);
					host_ips_hash.remove(host);
					ips_new_val=ips_old_val+ips_val;
					host_ips_hash.put(host, ips_new_val);
				}       		
        	}
        	
        	
			Enumeration host_enum=host_ips_hash.keys();
			
			while(host_enum.hasMoreElements())
			{
				host=host_enum.nextElement()+"";
				if((host==null)||(host.trim().equals("")))
				{
					continue;
				}
			    ips_val=host_ips_hash.get(host);
			    if(ips_val<1)
			    {
			    	continue;
			    }
			    host=host.trim();
			    
				if((host==null)||(host.equals("")))
				{
					continue;
				}
				result.set(host+"\001"+ips_val);
				context.write(key,result);					
			}    	
        }  
    }

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: SWAWordInfoMR <day> <input> <output>");
			System.exit(2);
		}
		String day = otherArgs[0];
		Job job = new Job(conf, "SWAWordInfoMR_" + day);
		job.setJarByClass(SWAWordInfoMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setCombinerClass(PrepareCombiner.class);  
		job.setNumReduceTasks(8);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}
	
	
	
	
}
