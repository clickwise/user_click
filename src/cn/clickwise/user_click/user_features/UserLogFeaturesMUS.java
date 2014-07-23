package cn.clickwise.user_click.user_features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

import cn.clickwise.admatch.MatchTool;
import cn.clickwise.liqi.file.uitls.JarFileReader;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.time.utils.TimeOpera;
import cn.clickwise.net.http.URIAnalysis;
import cn.clickwise.net.http.admatchtest.AdMatchTestBase;
import cn.clickwise.user_click.seg.AnsjSeg;

public class UserLogFeaturesMUS extends AdMatchTestBase {
	public AnsjSeg ansjseg = null;
	public String method = "/adduserrecord?s=";
	public Jedis jedis = null;

	public void init() {

		JarFileReader jfr = new JarFileReader();
		String seg_dict_file = "five_dict_uniq.txt";
		String stop_dict_file = "cn_stop_words_utf8.txt";
		HashMap<String, String> seg_dict = jfr.jarFile2Hash(seg_dict_file);
		HashMap<String, String> stop_dict = jfr.jarFile2Hash(stop_dict_file);
		ansjseg = new AnsjSeg();
		ansjseg.setSeg_dict(seg_dict);
		ansjseg.setStop_dict(stop_dict);

		jedis = new Jedis("106.187.35.172", 16379, 1000);// redis服务器地址
		jedis.select(14);

	}

	public String record2features(String uri, String head) {
		HashMap<String, String> param_map = MatchTool.convert_params(uri, head);
		String uid = "";
		String host = "";

		String title = "";

		uid = param_map.get("uid");
		host = param_map.get("host");
		title = param_map.get("title");

		if ((SSO.tioe(host)) && (SSO.tioe(title))) {
			return "";
		}

		return user_host(uid, host, title);
	}

	public String user_host(String uid, String host, String title) {
		JSONObject json = new JSONObject();

		try {

			if (SSO.tnoe(host)) {
				json.put("url_host", host);
			} else {
				json.put("url_host", "NA");
			}

			json.put("refer_host", "NA");

			json.put("uid", uid);
			json.put("datatype", "HOSTTITLE");
			json.put("time", (TimeOpera.getCurrentTimeLong() / 1000) + "");
			String cate = getCate(host);
			if (SSO.tioe(cate)) {
				cate = "NA";
			}
			json.put("host_cate", cate);
			JSONArray jsontitle = new JSONArray();

			if (SSO.tioe(title)) {
				jsontitle.put("NA");
			} else {
				String seg_title = ansjseg.seg(title);
				String[] seg_arr = seg_title.split("\\s+");
				if ((seg_arr == null) || (seg_arr.length < 1)) {
					jsontitle.put("NA");
				} else {
					for (int j = 0; j < seg_arr.length; j++) {
						if (SSO.tioe(seg_arr[j])) {
							continue;
						}
						jsontitle.put(seg_arr[j]);
					}
				}
			}

			json.put("title", jsontitle);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return json.toString();
	}

	public String getCate(String host) {
		String cate = "";
		Set<String> js = jedis
				.zrangeByScore(
						host,
						(long) ((double) TimeOpera
								.str2long("2014-02-08 15:29:05") / (double) 1000),
						(long) ((double) (TimeOpera.getCurrentTimeLong() + 100000) / (double) 1000));
		System.out.println(js.size());
		Iterator js_it = js.iterator();
		String[] seg_arr = null;
		String rec = "";
		while (js_it.hasNext()) {
			rec = js_it.next() + "";
			seg_arr = rec.split("\001");
			cate = jedis.get("md5_" + seg_arr[0]);
		}

		return cate;
	}

	public void traverse_log(File log) {
		FileReader fr = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;

		BufferedReader br = null;
		String record = "";
		String json_record = "";
		try {
			// fr=new FileReader(input_file);
			fis = new FileInputStream(log.getAbsolutePath());
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			while ((record = br.readLine()) != null) {
				json_record = record2features(record, "/AddUserRec?");
				if (SSO.tioe(json_record)) {
					continue;
				}
				addUserRecord(json_record);
			}
		} catch (Exception e) {
		}
	}

	public void addUserRecord(String text) {
		String encode_text = URLEncoder.encode(text);
		// System.out.println("encode_seg_s:"+encode_seg_s);
		encode_text = encode_text.replaceAll("\\s+", "");
		String url = url_prefix + method + encode_text;
		String response = hct.postUrl(url);
		System.out.println("response:" + response);
	}

	public String user_se() {

		return null;
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage:<UserLogFeatures> <log_file>");
			System.exit(1);
		}

		UserLogFeaturesMUS ulf = new UserLogFeaturesMUS();
		ulf.init();
		ulf.traverse_log(new File(args[0]));
	}
}
