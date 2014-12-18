package cn.clickwise.gaodong.secondsort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class BrowserAnalysisMR {
	public static class IntPair implements WritableComparable<IntPair> {
		private int first = 0;

		/**
		 * Set the left and right values.
		 */
		public void set(int right) {
			first = right;
		}

		public int getFirst() {
			return first;
		}

		/**
		 * Read the two integers. Encoded as: MIN_VALUE -> 0, 0 -> -MIN_VALUE,
		 * MAX_VALUE-> -1
		 */
		@Override
		public void readFields(DataInput in) throws IOException {
			first = in.readInt() + Integer.MIN_VALUE;
			
		}

		@Override
		public void write(DataOutput out) throws IOException {
			out.writeInt(first - Integer.MIN_VALUE);
			
		}

		@Override
		public int hashCode() {
			return first * 157 ;
		}

		@Override
		public boolean equals(Object right) {
			if (right instanceof IntPair) {
				IntPair r = (IntPair) right;
				return r.first == first;
			} else {
				return false;
			}
		}

		/** A Comparator that compares serialized IntPair. */
		public static class Comparator extends WritableComparator {
			public Comparator() {
				super(IntPair.class);
			}

			public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2,
					int l2) {
				return -compareBytes(b1, s1, l1, b2, s2, l2);
			}
		}

		static { // register this comparator
			WritableComparator.define(IntPair.class, new Comparator());
		}

		@Override
		public int compareTo(IntPair o) {
			if (first != o.first) {
				return first < o.first ? -1 : 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Partition based on the first part of the pair.
	 */
	public static class FirstPartitioner extends
			Partitioner<IntPair, Text> {
		@Override
		public int getPartition(IntPair key, Text value,
				int numPartitions) {
			return Math.abs(key.getFirst() * 127) % numPartitions;
		}
	}

	/**
	 * Compare only the first part of the pair, so that reduce is called once
	 * for each value of the first part.
	 */
	public static class FirstGroupingComparator implements
			RawComparator<IntPair> {
		@Override
		public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
			return WritableComparator.compareBytes(b1, s1, Integer.SIZE / 8,
					b2, s2, Integer.SIZE / 8);
		}

		@Override
		public int compare(IntPair o1, IntPair o2) {
			int l = o1.getFirst();
			int r = o2.getFirst();
			return l == r ? 0 : (l < r ? -1 : 1);
		}
	}

	private static class PrepareMaper extends
			Mapper<Object, Text, Text, IntWritable> {
		private Text word = new Text();
		private final static IntWritable one = new IntWritable(1);

		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString().trim();
			String regex = "\001";
			if (line != null && line.contains(regex)) {
				String[] browser_line = line.split(regex);
				if (browser_line.length == 11) {
					
					String browser=browser_line[10].toUpperCase();
					if(browser.contains("IPHONE")||browser.contains("IPAD")||browser.contains("IPOD")
							||browser.contains("ANDROID")||browser.contains("PHONE")||browser.contains("MOBILE")
							||browser.contains("OPERA MINI")||browser.contains("XIAO MI")||browser.contains("HUA WEI")||browser.contains("MIUI")) { 
						context.write(new Text("移动端:"+"\001"+browser_line[10]+"\001"), one);
					 } else{ 
						 context.write(new Text("PC端:"+"\001"+browser_line[10]+"\001"), one);
					 }
					 
					

				}

			}
			

		}
	}
	

	private static class PrepareReduce extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}

	}
	
	private static class SecondSortM extends Mapper<LongWritable, Text, IntPair, Text>{

	    private final IntPair key = new IntPair();
	    private final Text value = new Text();
	    
	    @Override
	    public void map(LongWritable inKey, Text inValue, 
	                    Context context) throws IOException, InterruptedException {
	      StringTokenizer itr = new StringTokenizer(inValue.toString(),"\001");
	      String left ="";
	      String mid="";
	      int right = 0;
	      if (itr.hasMoreTokens()) {
	    	  	left=itr.nextToken();
	        if (itr.hasMoreTokens()) {
	        	mid=itr.nextToken();
	        }
	        if (itr.hasMoreTokens()) {
	          right = Integer.parseInt(itr.nextToken().trim());
	          
	        }
	        key.set(right);
	        value.set("\001"+left+"\001"+mid);
	        
	        context.write(key, value);
	      }
	    }
	}
	
	private static class SecondSortR   extends Reducer<IntPair, Text, Text, NullWritable> {
	    /*private static final Text SEPARATOR = 
	    	      new Text("------------------------------------------------");*/
	    	    private final Text first = new Text();
	    	    
	    	    @Override
	    	    public void reduce(IntPair key, Iterable<Text> values,
	    	                       Context context
	    	                       ) throws IOException, InterruptedException {
	    	      //context.write(SEPARATOR, null);
	    	      
	    	      for(Text value: values) {
	    	    	first.set(Integer.toString(key.getFirst())+value);
	    	        context.write(first,NullWritable.get());
	    	      }
	    	    }
		
	}

	public static void main(String[] args) throws Throwable {
		Configuration config = new Configuration();
		String[] otherArgs = new GenericOptionsParser(config, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.out.println("Usage: BrowserAnalysisMR <input> <output>");
			System.exit(2);
		}

		Job job1 = new Job(config,"job1");
		job1.setJarByClass(BrowserAnalysisMR.class);

		job1.setMapperClass(PrepareMaper.class);
		job1.setCombinerClass(PrepareReduce.class);
		job1.setReducerClass(PrepareReduce.class);
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job1, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job1, new Path(otherArgs[1]));
		job1.waitForCompletion(true);
		Job job2 = new Job(config,"job1");
		job2.setJarByClass(BrowserAnalysisMR.class);

		job2.setMapperClass(SecondSortM.class);
		job2.setReducerClass(SecondSortR.class);
	    // group and partition by the first int in the pair
		job2.setPartitionerClass(FirstPartitioner.class);
		job2.setGroupingComparatorClass(FirstGroupingComparator.class);

	    // the map output is IntPair, IntWritable
		job2.setMapOutputKeyClass(IntPair.class);
		job2.setMapOutputValueClass(Text.class);

	    // the reduce output is Text, IntWritable
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job2, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job2, new Path(otherArgs[2]));
		
		System.exit(job2.waitForCompletion(true) ? 0 : 1);
	}
}
