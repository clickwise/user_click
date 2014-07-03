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

public class LineProcessMR {
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();

		private String local_params="";
		private String line_class_name="";
		public Method lineFunc=null;
		public Class  lineClass=null;
		
		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();
			local_params=conf.get("local_params");
			line_class_name=conf.get("line_class_name");
			try{
			lineClass = Class.forName("cn.clickwise.user_click.LineFunc."+line_class_name);
			lineFunc=lineClass.getMethod("lineProcess", new Class[]{String.class,String.class});
			}
			catch(Exception e)
			{
				
			}

		}
		
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String recline = value.toString().trim();
			if(SSO.tioe(recline))
			{
				return;
			}
			
			String processedLine="";
                          
                        /*
			double ran=Math.random();
                        if(ran>0.99)
                        {
                           System.out.println("recLine:"+recline);
                        }
                        */

			try{
			  processedLine=lineFunc.invoke(lineClass.newInstance(),recline, local_params)+"";
			}
			catch(Exception e)
			{
				
			}
                        if(SSO.tioe( processedLine))
                        {
                          return;
                        }
			String[] seg_arr= processedLine.split("\001");
                        if(seg_arr.length<1)
                        {
            	          return;
                        }
            
			String psline="";
			String keyVir="";
			
			keyVir=seg_arr[0]+"\001";
			for(int j=1;j<seg_arr.length-1;j++)
			{
				psline+=seg_arr[j]+"\001";
			}
			if(seg_arr.length>1)
			{
			  psline+=seg_arr[seg_arr.length-1];
			}
			
	  		word.set(keyVir);
	  		word1.set(psline);
	  		context.write(word, word1);
			
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
		if (otherArgs.length < 4) {
			System.err.println("Usage: LineProcessMR <reduce num> <input_hdfs> <output_hdfs> <Line_Process_ClassName> <params>*");
			System.exit(1);
		}

		int reduce_num=Integer.parseInt(otherArgs[0]);
		String line_class_name=otherArgs[3];
		
		String local_params="";
		
		for(int i=4;i<otherArgs.length;i++)
		{
			local_params+=(otherArgs+" ");
		}
		local_params=local_params.trim();
		conf.set("local_params", local_params);
		conf.set("line_class_name", line_class_name);
		Job job = new Job(conf, "LineProcessMR");
		job.setJarByClass(LineProcessMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(reduce_num);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
