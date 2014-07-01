package cn.clickwise.user_click.seg;

import java.io.IOException;
import java.util.HashMap;
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

import cn.clickwise.liqi.file.uitls.JarFileReader;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.basic.WordStatis;


public class LineWordCountMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public  int loc_text_index = 0;
		public  int loc_field_num = 0;

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();				
			loc_text_index=Integer.parseInt(conf.get("loc_text_index"));
			loc_field_num=Integer.parseInt(conf.get("loc_field_num"));
		}
		
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String recline = value.toString().trim();
			String[] seg_arr=recline.split("\001");
			String seg_text="";
			String word_statis="";
			
			String wsline="";
			String keyVir="";
			System.out.println("field_num:"+loc_field_num+"  seg_arr.length:"+seg_arr.length);
			if (seg_arr.length==loc_field_num) {    		
				seg_text=seg_arr[loc_text_index];
				if(SSO.tnoe(seg_text))
				{
					seg_text=seg_text.trim();
					word_statis=WordStatis.wordStatis(seg_text);
				  	if(SSO.tnoe(word_statis))
				  	{
				  		word_statis=word_statis.trim();
				  		keyVir=seg_arr[0]+"\001";
			            for(int j=1;j<loc_text_index;j++)
			            {
			            	wsline+=(seg_arr[j]+"\001");
			            }
			            wsline+=word_statis+"\001";
				  		for(int j=loc_text_index+1;j<loc_field_num;j++)
				  		{
				  			wsline+=(seg_arr[j]+"\001");
				  		}
				  		wsline=wsline.trim();
				  		word.set(keyVir);
				  		word1.set(wsline);
				  		context.write(word, word1);
				  	}
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
		if (otherArgs.length != 4) {
			System.err.println("Usage: LineWordCountMR  <filed_num> <text_index> <input> <output>");
			System.exit(1);
		}
		int field_num=Integer.parseInt(otherArgs[0]);
		int index = Integer.parseInt(otherArgs[1]);
		if(index>=field_num)
		{
			System.err.println("index must be smaller than field_num");
			System.exit(1);
		}
		conf.set("loc_text_index", index+"");
		conf.set("loc_field_num", field_num+"");
		Job job = new Job(conf, "LineWordCountMR");
		job.setJarByClass(LineWordCountMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(5);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[2]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[3]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}
