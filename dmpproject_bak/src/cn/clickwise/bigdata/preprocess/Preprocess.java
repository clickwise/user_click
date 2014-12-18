package cn.clickwise.bigdata.preprocess;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.clickwise.bigdata.preprocess.lib.AccessDataSource;
import cn.clickwise.bigdata.preprocess.lib.BaseDataSource;
import cn.clickwise.bigdata.preprocess.lib.NstatDataSource;
import cn.clickwise.bigdata.preprocess.lib.RstatDataSource;

/**
 * 
 * @author gao
 * 
 */

public class Preprocess {

	public static class PrepprocessMapper extends
			Mapper<Object, Text, Text, NullWritable> {

		private BaseDataSource bds;

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();
			String data_type = conf.get("data_type", null);
			System.err.println(data_type);
			
			//Create Data Source based on data_type
			switch (data_type) {
			case "nstat":
				String nstat_mode = conf.get("nstat_mode","cookie");
				String radius_host_port = conf.get("radius_host_port", "127.0.0.1:8000");
				bds = new NstatDataSource(nstat_mode,radius_host_port);
				break;
			case "rstat":
				bds = new RstatDataSource();
				break;
			case "access":
				bds = new AccessDataSource();
				break;
			default:
				System.err.println("Unknown data type:" + data_type);
				break;
			}
		}

		private Text word = new Text();

		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			if (bds == null)
				return;

			String res = bds.process_oneline(value.toString());
			if (res != null && res.length() > 10) {
				word.set(res);
				context.write(word, NullWritable.get());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length < 3) {
			System.err.println("Usage: preprocess <data_type> <in> <out> [<cookie> |<radius> <radis_server_host:port> ");
			System.exit(-1);
		}
		// data type
		String data_type = otherArgs[0];
		conf.set("data_type", data_type);

		//Check extra parameters for nstat mode
		if (data_type.equals("nstat")) {
			String nstat_mode = null;
			String radius_host_port = null;
			if (otherArgs.length >= 4) {
				conf.set("nstat_mode", nstat_mode);
				nstat_mode = otherArgs[3];
			}
			if (nstat_mode.equals("radius")) {
				if (otherArgs.length < 5){
					System.err.println("Required radius host and port info for nstat radius mode");
					System.exit(-1);
				}
				radius_host_port = otherArgs[4];
				conf.set("radius_host_port", radius_host_port);
			}
		}
		
		//Setup MapReduce for preprocessing
		Job job = new Job(conf, "preprocess");
		job.setJarByClass(Preprocess.class);
		job.setMapperClass(PrepprocessMapper.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
		System.out.println(otherArgs[0]);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
