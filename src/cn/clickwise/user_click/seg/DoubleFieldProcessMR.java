package cn.clickwise.user_click.seg;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

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

public class DoubleFieldProcessMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public  int loc_text_index1 = 0;
		public  int loc_text_index2 = 0;
		public  int loc_field_num = 0;
		public String double_field_func_name="";
		public String double_field_func_params="";
		public Method doubleFieldFunc=null;
		public Class  doubleFieldClass=null;
		
		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();				
			loc_text_index1=Integer.parseInt(conf.get("loc_text_index1"));
			loc_text_index2=Integer.parseInt(conf.get("loc_text_index2"));
			loc_field_num=Integer.parseInt(conf.get("loc_field_num"));
			double_field_func_name=conf.get("double_field_func_name");
			double_field_func_params=conf.get("double_field_func_params");
			try{
				doubleFieldClass = Class.forName("cn.clickwise.user_click.field."+double_field_func_name);
				doubleFieldFunc=doubleFieldClass.getMethod("doubleFieldFunc", new Class[]{String.class,String.class,String.class});
			}
			catch(Exception e)
			{
				
			}
		}
		
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String recline = value.toString().trim();
			String[] seg_arr=recline.split("\001");
			String field_text1="";
			String field_text2="";
			String double_field_processed="";
			
			String psline="";
			String wsline="";
			String keyVir="";
			//System.out.println("field_num:"+loc_field_num+"  seg_arr.length:"+seg_arr.length);
			
			String[] seg_arr_processed=null;
			String[] seg_arr_kv=null;
			
			if (seg_arr.length==loc_field_num) {    		
				field_text1=seg_arr[loc_text_index1];
				field_text2=seg_arr[loc_text_index2];
				
				field_text1=field_text1.trim();
				field_text2=field_text2.trim();
				
					try{
					double_field_processed=doubleFieldFunc.invoke(doubleFieldClass.newInstance(),field_text1,field_text2, double_field_func_params)+"";
					}
					catch(Exception e)
					{
						
					}
				  	if(SSO.tnoe(double_field_processed))
				  	{
				  		//double_field_processed=double_field_processed.trim();
				  		seg_arr_processed=double_field_processed.split("\001");
				  		
				  		if(seg_arr_processed.length!=2)
				  		{
				  			return;
				  		}
				        for(int j=0;j<loc_text_index1;j++)
				        {
				        	psline+=(seg_arr[j]+"\001");
				        }
				        psline+=seg_arr_processed[0]+"\001";
				        
				        for(int j=(loc_text_index1+1);j<loc_text_index2;j++)
				        {
				        	psline+=(seg_arr[j]+"\001");
				        } 
				        
				        psline+=seg_arr_processed[1]+"\001";
				        
				        for(int j=(loc_text_index2+1);j<loc_field_num;j++)
				        {
				        	psline+=(seg_arr[j]+"\001");
				        }  
				        
				        psline=psline.trim();
				        
				        seg_arr_kv=psline.split("\001");
				        if(seg_arr_kv.length!=loc_field_num)
				        {
				        	return;
				        }
				        				        
				  		keyVir=seg_arr_kv[0]+"\001";
			            for(int j=1;j<loc_field_num;j++)
			            {
			            	wsline+=(seg_arr_kv[j]+"\001");
			            }
				  		wsline=wsline.trim();
				  					  		
				  		word.set(keyVir);
				  		word1.set(wsline);
				  		context.write(word, word1);
				  	}
				
			}
		}//map
			
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
					//if (SSO.tnoe(info)) {
						result_key.set((key_str + "\001" + info).trim());
						context.write(result_key, NullWritable.get());
					//}
				}
			}
		}
	}
     
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length < 6) {
			System.err.println("Usage: DoubleFieldProcessMR  <filed_num> <text_index1> <text_index2> <input> <output> <field func name> <params>*");
			System.exit(1);
		}
		int field_num=Integer.parseInt(otherArgs[0]);
		int index1 = Integer.parseInt(otherArgs[1]);
		int index2 = Integer.parseInt(otherArgs[2]);
		if(index1>=field_num||index2>=field_num)
		{
			System.err.println("index must be smaller than field_num");
			System.exit(1);
		}
		
		String double_field_func_name=otherArgs[5];
		String double_field_func_params="";
		for(int i=6;i<otherArgs.length;i++)
		{
			double_field_func_params+=(otherArgs[i]+" ");
		}
		double_field_func_params=double_field_func_params.trim();
		
		conf.set("loc_text_index1", index1+"");
		conf.set("loc_text_index2", index2+"");
		conf.set("loc_field_num", field_num+"");
		conf.set("double_field_func_name", double_field_func_name);
		conf.set("double_field_func_params", double_field_func_params);
		
		Job job = new Job(conf, "DoubleFieldProcessMR");
		job.setJarByClass(DoubleFieldProcessMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(5);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[3]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[4]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}
