package cn.clickwise.baidu_hot;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class BKWMergeLinksMR {

	private static class PPrepareMapper extends
			Mapper<Object, Text, Text, Text> {
		private Text word = new Text();
		private Text word1 = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String val = value.toString();
			String arr[] = val.split("\001");
			String keyword = "";
			String code_url = "";
			String pvs = "";
			String uvs = "";
			if (arr.length == 4) {
				keyword = arr[0];
				code_url = arr[1];
				pvs = arr[2];
				uvs = arr[3];
				word.set(keyword);
				word1.set(code_url + "\001" + pvs);
				context.write(word, word1);

			}

		}
	}

	private static class PPrepareReducer extends
			Reducer<Text, Text, Text, Text> {
		private Text result = new Text();
		private Text word_key = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			String keyword = key.toString();

			Iterator<Text> it = values.iterator();
			String temp_s = "";
			String[] seg_arr = null;

			String code_url = "";
			String pvs = "";

			String url_s = "";
			url_s = "";
			Vector ips_urls = new Vector();

			while (it.hasNext()) {
				temp_s = it.next().toString();
				temp_s = temp_s.trim();
				if (temp_s.length() < 1) {
					continue;
				}
				seg_arr = temp_s.split("\001");
				if (seg_arr.length != 2) {
					continue;
				}

				code_url = seg_arr[0].trim();
				pvs = seg_arr[1].trim();
				// url_s=url_s+url+" ";
				ips_urls.add(pvs + "\001" + code_url);
			}

			Vector n_ips_urls = null;
			n_ips_urls = rankVector(ips_urls);
			String[] n_seg_arr = null;
			String temp_ips_url = "";
			for (int i = 0; i < n_ips_urls.size(); i++) {
				temp_ips_url = n_ips_urls.get(i) + "";
				temp_ips_url = temp_ips_url.trim();
				n_seg_arr = temp_ips_url.split("\001");
				if (n_seg_arr.length < 2) {
					continue;
				}
				url_s = url_s + n_seg_arr[1] + " ";
			}

			//System.out.println("url_s:" + url_s);
			url_s = url_s.trim();		
			if ((url_s != null)&&(keyword!=null) &&!(url_s.equals(""))) {
					result = new Text();
					word_key = new Text();
					word_key.set(keyword);
					result.set(url_s);
					context.write(word_key, result);
				
			}
		}
	}

	public static Vector rankVector(Vector ips_urls) {
		Vector nv = new Vector();
		int minindex = -1;
		String tempS = "";
		String minword = "";
		String tempword = "";

		String temp_ips_url = "";
		String[] seg_arr = null;

		if (ips_urls.size() < 10) {
			return ips_urls;
		}
		for (int i = 0; i < 10; i++) {
			if (i == (ips_urls.size() - 2)) {
				break;
			}
			tempS = ips_urls.get(i) + "";
			tempS = tempS.trim();
			seg_arr = tempS.split("\001");
			if (seg_arr.length < 2) {
				continue;
			}
			minword = seg_arr[0];
			minindex = i;
			for (int j = i; j < ips_urls.size(); j++) {
				tempS = ips_urls.get(j) + "";
				tempS = tempS.trim();
				seg_arr = tempS.split("\001");
				if (seg_arr.length < 2) {
					continue;
				}
				tempword = seg_arr[0];
				if (Integer.parseInt(tempword) > Integer.parseInt(minword)) {
					minindex = j;
					minword = tempword;
				}
			}
			temp_ips_url = ips_urls.get(i) + "";
			ips_urls.set(i, ips_urls.get(minindex) + "");
			nv.add(ips_urls.get(minindex));
			ips_urls.set(minindex, temp_ips_url);
		}

		return nv;
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: BKWMergeLinksMR <day> <input> <output>");
			System.exit(2);
		}

		String day = otherArgs[0];
		Job job = new Job(conf, "BKWMergeLinksMR_" + day);
		job.setJarByClass(BKWMergeLinksMR.class);
		job.setMapperClass(PPrepareMapper.class);
		job.setReducerClass(PPrepareReducer.class);
		job.setNumReduceTasks(1);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
