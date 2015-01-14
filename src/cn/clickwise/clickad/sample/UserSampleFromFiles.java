package cn.clickwise.clickad.sample;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import cn.clickwise.lib.string.FileToArray;
import cn.clickwise.lib.string.SSO;

public class UserSampleFromFiles {

	public HashMap<String, String> user_host_info(String file) {
		HashMap<String, String> user_host = new HashMap<String, String>();
		try {
			String[] lines = FileToArray.fileToDimArr(file);
			String line = "";
			String[] fields = null;
			String cookie = "";
			String cate = "";
			String words = "";
			for (int i = 0; i < lines.length; i++) {
				line = lines[i];
				fields = line.split("\t");
				if (fields == null) {
					continue;
				}
				if (fields.length < 4) {
					continue;
				}
				cookie = fields[0];
				cate = fields[1];
				cate = cate.replaceAll("\\|", " ");
				words = fields[3];
				words = words.replaceAll(":\\d+", "");
				if (SSO.tioe(cookie)) {
					continue;
				}
				cookie = cookie.trim();
				if (!(user_host.containsKey(cookie))) {
					user_host.put(cookie, cate + " " + words + " ");
				} else {
					user_host.put(cookie, user_host.get(cookie) + " " + cate
							+ " " + words + " ");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return user_host;
	}

	public HashMap<String, String> user_se_info(String file) {
		HashMap<String, String> user_se = new HashMap<String, String>();
		try {
			String[] lines = FileToArray.fileToDimArr(file);
			String line = "";
			String[] fields = null;
			String cookie = "";

			String words = "";
			for (int i = 0; i < lines.length; i++) {
				line = lines[i];
				fields = line.split("\t");
				cookie = fields[0];
				words = fields[3];
				if (SSO.tioe(cookie)) {
					continue;
				}
				cookie = cookie.trim();
				if (!(user_se.containsKey(cookie))) {
					user_se.put(cookie, words + " ");
				} else {
					user_se.put(cookie, user_se.get(cookie) + " " + words + " ");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user_se;
	}

	public HashMap<String, String> mergeHostSe(
			HashMap<String, String> user_host, HashMap<String, String> user_se) {
		HashMap<String, String> user_info = new HashMap<String, String>();
		for (Map.Entry<String, String> m : user_host.entrySet()) {
			if (!(user_info.containsKey(m.getKey()))) {
				user_info.put(m.getKey(), m.getValue());
			}
		}

		for (Map.Entry<String, String> m : user_se.entrySet()) {
			if (!(user_info.containsKey(m.getKey()))) {
				user_info.put(m.getKey(), m.getValue());
			} else {
				user_info.put(m.getKey(),
						user_info.get(m.getKey()) + " " + m.getValue());
			}
		}

		return user_info;
	}

	public static void main(String[] args) {
		UserSampleFromFiles usff = new UserSampleFromFiles();
		String host_file = "info/lancome_cate.txt";
		HashMap<String, String> user_host = usff.user_host_info(host_file);

		String se_file = "info/lancome_se.txt";
		HashMap<String, String> user_se = usff.user_host_info(se_file);
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("info/user_info.txt"));
			HashMap<String, String> user_info = usff.mergeHostSe(user_host,
					user_se);
			for (Map.Entry<String, String> m : user_info.entrySet()) {
               pw.println(m.getKey()+"\001"+m.getValue());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
