package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;

import redis.clients.jedis.Jedis;

public class BKWHotWordsMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {
		private Text word = new Text();
		private Text word1 = new Text();
		public Jedis jedis;
		public Jedis cated_redis;

		public String redis_host_ip = "";
		public String redis_cated_words_ip = "";
		public int redis_port = 6379;
		public int redis_host_cate_db = 0;
		public int redis_cated_words_db = 0;
		public boolean useProxy=false;

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String val = value.toString();
			// System.out.println("val:"+val);
			String[] kud_seg = null;
			try {
				load_config();
			} catch (Exception e) {

			}
			if (val != null) {
				kud_seg = val.split("\001");
			}
			Vector urls_vec = null;
			Vector urls_top_vec = null;
			String keyword = "";
			int uvs = 0;
			String temp_url_info = "";
			Vector trueurls_top_vec;

			jedis = new Jedis(redis_host_ip, redis_port, 100000);// redis服务器地址
			jedis.ping();
			jedis.select(redis_host_cate_db);

			cated_redis = new Jedis(redis_cated_words_ip, redis_port, 100000);
			cated_redis.ping();
			cated_redis.select(redis_cated_words_db);

			String stcate = "";
			String url_mos = "";
			String sturl = "";
			String url_s = "";
			double ran;
			int rani;
			String[] seg_urls = null;
			String oldcate = "";
			String url_sts = "";
			if (kud_seg != null && (kud_seg.length >= 3)) {
				urls_vec = new Vector();
				keyword = kud_seg[0].trim();
				uvs = Integer.parseInt(kud_seg[1].trim());
				url_s = kud_seg[2].trim();
				if ((keyword != null) && !(keyword.equals(""))) {

					try {
						oldcate = cated_redis.get(keyword);
					} catch (Exception re) {
						ran = Math.random();
						// System.out.println("ran:" + ran);
						rani = -1;
						rani = (int) (ran * 10000);
						Thread.sleep(rani);

					}
					if (oldcate == null || ((oldcate.trim()).length() == 0)) {
						if (!(url_s.equals(""))) {

							seg_urls = url_s.split("\\s+");
							System.out.println("seg_urls.length:"
									+ seg_urls.length);
							if ((seg_urls != null) && (seg_urls.length > 0)) {
								for (int j = 0; j < seg_urls.length; j++) {
									temp_url_info = seg_urls[j].trim();
									if (!(temp_url_info.equals(""))) {
										urls_vec.add(temp_url_info);
									}
								}

								// urls_top_vec = rankVector(urls_vec, 10);
								System.out.println("urls_vec.size:"
										+ urls_vec.size());
								trueurls_top_vec = getTrueUrls(urls_vec);
								try {
									stcate = predict_preliminar(trueurls_top_vec);
								} catch (Exception e) {
								}

								if (isNovel(keyword, stcate)) {
									url_mos = "\001小说\001" + uvs + "\001";
									url_sts = "小说\001" + uvs + "\001";
									for (int ti = 0; ti < trueurls_top_vec
											.size(); ti++) {
										sturl = trueurls_top_vec.get(ti) + "";
										sturl = sturl.trim();
										url_mos = url_mos + sturl + "\001";
										url_sts = url_sts + sturl + "\001";
									}
									try {
										url_sts = url_sts.trim();
										cated_redis.set(keyword, url_sts);
									} catch (Exception re) {
										ran = Math.random();
										// System.out.println("ran:" + ran);
										rani = -1;
										rani = (int) (ran * 10000);
										Thread.sleep(rani);
									}

									word.set(keyword);
									word1.set(url_mos);
									context.write(word, word1);
								} else if (isNews(keyword, stcate)) {
									url_mos = "\001新闻资讯\001" + uvs + "\001";
									url_sts = "新闻资讯\001" + uvs + "\001";
									for (int ti = 0; ti < trueurls_top_vec
											.size(); ti++) {
										sturl = trueurls_top_vec.get(ti) + "";
										sturl = sturl.trim();
										url_mos = url_mos + sturl + "\001";
										url_sts = url_sts + sturl + "\001";
									}
									try {
										url_sts = url_sts.trim();
										cated_redis.set(keyword, url_sts);
									} catch (Exception re) {
										ran = Math.random();
										// System.out.println("ran:" + ran);
										rani = -1;
										rani = (int) (ran * 10000);
										Thread.sleep(rani);
									}
									word.set(keyword);
									word1.set(url_mos);
									context.write(word, word1);
								} else if (isVideo(keyword, stcate)) {
									url_mos = "\001视频\001" + uvs + "\001";
									url_sts = "视频\001" + uvs + "\001";
									for (int ti = 0; ti < trueurls_top_vec
											.size(); ti++) {
										sturl = trueurls_top_vec.get(ti) + "";
										sturl = sturl.trim();
										url_mos = url_mos + sturl + "\001";
										url_sts = url_sts + sturl + "\001";
									}
									word.set(keyword);
									word1.set(url_mos);
									context.write(word, word1);
								} else {
									if ((keyword != null)
											&& (!keyword.equals(""))) {
										url_mos = "\001噪音\001" + uvs + "\001";
										url_sts = "噪音\001" + uvs + "\001";
										for (int ti = 0; ti < trueurls_top_vec
												.size(); ti++) {
											sturl = trueurls_top_vec.get(ti)
													+ "";
											sturl = sturl.trim();
											url_mos = url_mos + sturl + "\001";
											url_sts = url_sts + sturl + "\001";
										}
										word.set(keyword);
										word1.set(url_mos);
										// context.write(word, word1);
										try {
											url_sts = url_sts.trim();
											cated_redis.set(keyword, url_sts);
										} catch (Exception re) {
											ran = Math.random();
											// System.out.println("ran:" + ran);
											rani = -1;
											rani = (int) (ran * 10000);
											Thread.sleep(rani);
										}
									}
								}

							}
						}
					} else {
						String old_cs = "";
						oldcate = oldcate.trim();
						String[] old_seg = oldcate.split("\001");
						String oldCateName = "";
						String old_url_s = "";
						String old_redis_s = "";
						if (old_seg.length > 2) {
							for (int oj = 2; oj < old_seg.length; oj++) {
								old_url_s = old_url_s + old_seg[oj] + "\001";
							}
							old_url_s = old_url_s.trim();
							oldCateName = old_seg[0].trim();

							if (oldCateName.equals("小说")) {

								old_cs = "\001小说\001" + uvs + "\001"
										+ old_url_s;
								old_redis_s = "小说\001" + uvs + "\001"
										+ old_url_s;
								try {
									cated_redis.set(keyword, old_redis_s);
								} catch (Exception re) {
									ran = Math.random();
									// System.out.println("ran:" + ran);
									rani = -1;
									rani = (int) (ran * 10000);
									Thread.sleep(rani);
								}
								word = new Text();
								word1 = new Text();
								word.set(keyword);
								word1.set(old_cs);
								context.write(word, word1);

							} else if (oldCateName.equals("新闻资讯")) {

								old_cs = "\001新闻资讯\001" + uvs + "\001"
										+ old_url_s;
								old_redis_s = "新闻资讯\001" + uvs + "\001"
										+ old_url_s;
								try {
									cated_redis.set(keyword, old_redis_s);
								} catch (Exception re) {
									ran = Math.random();
									// System.out.println("ran:" + ran);
									rani = -1;
									rani = (int) (ran * 10000);
									Thread.sleep(rani);
								}
								word = new Text();
								word1 = new Text();
								word.set(keyword);
								word1.set(old_cs);
								context.write(word, word1);
							} else if (oldCateName.equals("影视剧")
									|| oldCateName.equals("电影")
									|| oldCateName.equals("综艺")) {
								old_cs = "\001视频\001" + uvs + "\001"
										+ old_url_s;
								old_redis_s = "视频\001" + uvs + "\001"
										+ old_url_s;
								word = new Text();
								word1 = new Text();
								word.set(keyword);
								word1.set(old_cs);
								context.write(word, word1);
							} else if (oldCateName.equals("噪音")) {
								old_cs = "\001噪音\001" + uvs + "\001"
										+ old_url_s;
								old_redis_s = "噪音\001" + uvs + "\001"
										+ old_url_s;
								word = new Text();
								word1 = new Text();
								word.set(keyword);
								word1.set(old_cs);
								context.write(word, word1);
							}
						}
					}

				}

			}
			/*
			 * String host=""; String url=""; String anchor_text=""; String
			 * pvs=""; String uvs=""; String ips=""; if(arr.length==6) {
			 * host=arr[0]; url=arr[1]; anchor_text=arr[2]; pvs=arr[3];
			 * uvs=arr[4]; ips=arr[5]; word.set(anchor_text);
			 * word1.set(host+"\001"+url+"\001"+pvs+"\001"+uvs+"\001"+ips);
			 * context.write(word, word1);
			 * 
			 * }
			 */

		}

		public Vector rankVector(Vector urls_ips, int topN) {
			Vector nv = new Vector();
			int minindex = -1;
			String tempS = "";
			String minword = "";
			String tempword = "";

			String temp_ips_url = "";
			String[] seg_arr = null;

			if (urls_ips.size() < topN) {
				return urls_ips;
			}
			for (int i = 0; i < topN; i++) {
				if (i == (urls_ips.size() - 2)) {
					break;
				}
				tempS = urls_ips.get(i) + "";
				tempS = tempS.trim();
				seg_arr = tempS.split("\t");
				if (seg_arr.length < 2) {
					continue;
				}
				minword = seg_arr[1];
				minindex = i;
				for (int j = i; j < urls_ips.size(); j++) {
					tempS = urls_ips.get(j) + "";
					tempS = tempS.trim();
					seg_arr = tempS.split("\t");
					if (seg_arr.length < 2) {
						continue;
					}
					tempword = seg_arr[1];
					if (Integer.parseInt(tempword) > Integer.parseInt(minword)) {
						minindex = j;
						minword = tempword;
					}
				}
				temp_ips_url = urls_ips.get(i) + "";
				urls_ips.set(i, urls_ips.get(minindex) + "");
				nv.add(urls_ips.get(minindex));
				urls_ips.set(minindex, temp_ips_url);
			}

			return nv;
		}

		public Vector getTrueUrls(Vector urls_top_vec) {
			Vector nv = new Vector();
			String codeurl = "";
			String turl = "";
			String[] seg_arr = null;
			String ourl = "";
			for (int i = 0; i < urls_top_vec.size(); i++) {
				codeurl = urls_top_vec.get(i) + "";
				codeurl = codeurl.trim();
				// seg_arr = codeurl.split("\t");
				/*
				 * if (seg_arr.length < 2) { continue; }
				 */
				if (codeurl.equals("")) {
					continue;
				}
				ourl = codeurl;
				turl = getRedirect(ourl);
				if (turl != null) {
					nv.add(turl);
				}
			}
			return nv;
		}

		private String getRedirect(String code_url) {

			String[] proxy_hosts = { "122.72.56.151", "122.72.56.152",
					"122.72.56.153", "122.72.102.60", "122.72.111.92",
					"122.72.111.98", "122.72.76.131", "122.72.76.132",
					"122.72.76.133", "122.72.11.129", "122.72.11.130",
					"122.72.11.131", "122.72.11.132", "122.72.99.2",
					"122.72.99.3", "122.72.99.4", "122.72.99.8" };
			String red_url = "";
			DefaultHttpClient httpclient = null;
			code_url = code_url.trim();
			String url = "";
			if (code_url.indexOf("http://") == -1) {
				url = "http://" + code_url;
			} else {
				url = code_url;
			}
			try {
				httpclient = new DefaultHttpClient();
				
				if(useProxy==true)
				{
				  double ran = Math.random();
				  System.out.println("ran:" + ran);
				  int rani = -1;
				  rani = (int) (ran * 16);
				  HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
				  httpclient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);				
				}			
				
				// httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,20000);
				httpclient.setRedirectStrategy(new RedirectStrategy() { // 设置重定向处理方式

							@Override
							public boolean isRedirected(HttpRequest arg0,
									HttpResponse arg1, HttpContext arg2)
									throws ProtocolException {

								return false;
							}

							@Override
							public HttpUriRequest getRedirect(HttpRequest arg0,
									HttpResponse arg1, HttpContext arg2)
									throws ProtocolException {

								return null;
							}
						});

				// 创建httpget.
				if ((isValidUrl(url)) == false) {
					return null;
				}
				HttpGet httpget = new HttpGet(url);
				// 执行get请求.
				HttpResponse response = httpclient.execute(httpget);

				int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode == HttpStatus.SC_OK) {
					// 获取响应实体
					// HttpEntity entity = response.getEntity();
					// if (entity != null) {
					// 打印响应内容长度
					// System.out.println("Response content length: "
					// + entity.getContentLength());
					// 打印响应内容
					// System.out.println("Response content: "
					// + EntityUtils.toString(entity));
					// }
				} else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY
						|| statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {

					// System.out.println("当前页面发生重定向了---");

					Header[] headers = response.getHeaders("Location");
					if (headers != null && headers.length > 0) {
						String redirectUrl = headers[0].getValue();
						red_url = redirectUrl;
						System.out.println("重定向的URL:" + redirectUrl);
						/*
						 * redirectUrl = redirectUrl.replace(" ", "%20");
						 * get(redirectUrl);
						 */
					}
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// 关闭连接,释放资源
				httpclient.getConnectionManager().shutdown();
			}

			return red_url;
		}

		public String predict_preliminar(Vector trueurls_top_vec) {
			String preli_cate = "NA";
			URI sturi = null;
			String sturl = "";
			String sthost = "";
			Hashtable tag_res = new Hashtable();
			String cate_res = "";
			int old_res_c = 0;
			for (int i = 0; i < trueurls_top_vec.size(); i++) {
				sturl = trueurls_top_vec.get(i) + "";
				sturl = sturl.trim();
				try {
					sturi = new URI(sturl);
					sthost = sturi.getHost();
				} catch (Exception e) {
				}

				if (sthost == null) {
					continue;
				}

				if (sthost.equals("")) {
					continue;
				}
				sthost = sthost.trim();
				try {
					cate_res = jedis.get(sthost);
				} catch (Exception ec) {
					try {
						double ran = Math.random();
						// System.out.println("ran:" + ran);
						int rani = -1;
						rani = (int) (ran * 10000);
						Thread.sleep(rani);
					} catch (Exception ei) {

					}
				}
				if (cate_res == null) {
					continue;
				}
				cate_res = cate_res.trim();
				if (!tag_res.containsKey(cate_res)) {
					tag_res.put(cate_res, 1);
				} else {
					old_res_c = Integer.parseInt(tag_res.get(cate_res) + "");
					old_res_c++;
					tag_res.remove(cate_res);
					tag_res.put(cate_res, old_res_c);
				}
			}

			Enumeration tag_keys = tag_res.keys();
			String maxTag = "NA";
			int maxTagCount = 0;
			String temp_key = "";
			int temp_count = 0;
			while (tag_keys.hasMoreElements()) {
				temp_key = tag_keys.nextElement() + "";
				// System.out.println("temp_key:"+temp_key);
				temp_count = Integer.parseInt(tag_res.get(temp_key) + "");
				if (temp_count > maxTagCount) {
					maxTagCount = temp_count;
					maxTag = temp_key;
				}
			}

			if (maxTag.equals("-1")) {
				preli_cate = "NA";
			} else {
				preli_cate = maxTag;
			}
			return preli_cate;
		}

		public boolean isNovel(String word, String preli_cate) {
			boolean isnov = false;
			String tag = preli_cate;
			if (tag.equals("小说") && word.length() > 1
					&& !Pattern.matches(".*网", word)
					&& !Pattern.matches(".*?文章.*", word)
					&& !Pattern.matches(".*?全本.*", word)
					&& !Pattern.matches(".*?小说.*", word)
					&& !Pattern.matches(".*?电子书.*", word)
					&& !Pattern.matches(".*?评书.*", word)
					&& !Pattern.matches(".*?书院.*", word)
					&& !Pattern.matches(".*?>书院.*", word)
					&& !Pattern.matches(".*?动漫.*", word)
					&& !Pattern.matches(".*?看书.*", word)
					&& !Pattern.matches("[a-zA-Z0-9]*", word)
					&& !Pattern.matches(".*?翻译.*", word)
					&& !Pattern.matches(".*?经典.*", word)
					&& !Pattern.matches(".*?日志.*", word)
					&& !Pattern.matches(".*?文学.*", word)
					&& !Pattern.matches(".*?短语.*", word)
					&& !Pattern.matches(".*?名言.*", word)
					&& !Pattern.matches(".*?红袖添香.*", word)
					&& !Pattern.matches(".*?句子.*", word)) {
				isnov = true;
			}

			return isnov;
		}

		public boolean isVideo(String word, String preli_cate) {
			boolean isvid = false;
			preli_cate = preli_cate.trim();
			if (preli_cate.equals("视频")) {
				isvid = true;
			}
			return isvid;
		}

		public boolean isNews(String word, String preli_cate) {
			boolean isvid = false;
			preli_cate = preli_cate.trim();
			if (preli_cate.equals("新闻资讯") && (word.length() > 3)
					&& (!Pattern.matches("[0-9a-zA-Z\\.]*", word))
					&& (word.indexOf("官网") == -1)
					&& (word.indexOf("参考消息") == -1)
					&& (word.indexOf("新浪") == -1) && (word.indexOf("凤凰") == -1)
					&& (word.indexOf("日报") == -1) && (word.indexOf("早报") == -1)
					&& (!word.equals("红十字会")) && (!word.equals("腾讯微博"))
					&& (!word.equals("新浪微博")) && (!word.equals("搜狐新闻"))
					&& (!word.equals("南方周末")) && (!word.equals("腾讯新闻"))
					&& (!word.equals("快乐男声"))) {
				isvid = true;
			}
			return isvid;
		}

		public void load_config() throws Exception {

			InetAddress addr = InetAddress.getLocalHost();
		    String ip = addr.getHostAddress().toString();// 获得本机IP
		    String address = addr.getHostName().toString();// 获得本机名
		    address = address.trim();
				
		    InputStream model_is=null;
			Properties prop = new Properties();
			// URL is = this.getClass().getResource("conf/config.properties");
			if(address.startsWith("adt"))
			{
		      model_is = this.getClass().getResourceAsStream(
					"/jbkw_config.properties");
			}
			else if(address.startsWith("zjdx"))
			{
			  model_is = this.getClass().getResourceAsStream(
					"/jbkw_config_dx.properties");
			}
			prop.load(model_is);

			redis_host_ip = prop.getProperty("redis_host_ip");
			redis_cated_words_ip = prop.getProperty("redis_cated_words_ip");
			redis_port = Integer.parseInt(prop.getProperty("redis_port"));
			redis_host_cate_db = Integer.parseInt(prop
					.getProperty("redis_host_cate_db"));
			redis_cated_words_db = Integer.parseInt(prop
					.getProperty("redis_cated_words_db"));
			useProxy=Boolean.parseBoolean(prop
					.getProperty("use_proxy"));

		}

		public boolean isValidUrl(String url) {
			boolean isVal = true;
			if ((url.indexOf("'") != -1) || (url.indexOf("}") != -1)) {
				return false;
			}

			return isVal;
		}

	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Text> it = values.iterator();

			while (it.hasNext()) {
				context.write(key, it.next());
			}

		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: BKWHotWordsMR <day> <input> <output>");
			System.exit(2);
		}

		String day = otherArgs[0];
		JobConf job_conf = new JobConf(conf, BKWHotWordsMR.class);

		job_conf.setNumReduceTasks(0);

		Job job = new Job(job_conf, "BKWHotWordsMR_" + day);
		job.setJarByClass(BKWHotWordsMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);

		job.setNumReduceTasks(2);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
