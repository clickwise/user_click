package cn.clickwise.clickad.jd_opinion;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;

import cn.clickwise.lib.string.SSO;

public class SEClickMR {

	private static class PPrepareMapper extends
			Mapper<Object, Text, Text, IntWritable> {
		
		private Text word = new Text();
		private static IntWritable valueOne = new IntWritable(1);

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
			String val = value.toString();
			ParseResult pr = null;
			pr = SEUrlParse.parseItem(val);
		
		    if (pr.isNull()) {
					return;
			}
			pr.decode();

			if (pr.isInValid()) {
					return;
			}
			
			String rs=pr.toString();
			
			String[] tokens=rs.split(";");
			if(tokens==null||tokens.length<1)
			{
				return;
			}
			
			String token="";
			for(int i=0;i<tokens.length;i++)
			{
				token=tokens[i];
				if(SSO.tioe(token))
				{
					continue;
				}
				token=token.trim();
				word.set(token);
				context.write(word, valueOne);
			}
			

		}
	}

	private static class PPrepareReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private Text result = new Text();
		private Text word_key = new Text();

		protected void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			String keyword = key.toString();

			Iterator<IntWritable> it = values.iterator();
			int sum=0;
			while (it.hasNext())
	        {
	            sum += it.next().get();
	        }
			context.write(key, new IntWritable(sum));
		}
	}

}
