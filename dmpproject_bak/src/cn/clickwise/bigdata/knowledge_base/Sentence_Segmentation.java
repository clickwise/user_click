package cn.clickwise.bigdata.knowledge_base;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

/**
 * 输入普通文本，输出分词结果。
 * 
 * @author chenshunfeng
 * 
 */
public class Sentence_Segmentation {

	private boolean use_seg_server = false;

	private int seg_port = 8092;

	private String seg_server = "";

	static Properties prop = new Properties();

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 *            ：待判断字符串
	 * @return：判断结果（true或false）
	 */

	public static boolean tnoe(String str) {
		boolean istnoe = false;
		if ((str != null) && (!(str.trim().equals("")))) {
			istnoe = true;
		}

		return istnoe;
	}

	/**
	 * 设置配置信息
	 */
	public void set_config() {
		// Properties prop=new Properties();
		prop.setProperty("seg_server", "192.168.110.186");
		prop.setProperty("seg_port", "8092");
		prop.setProperty("use_seg_server", "true");
	}

	/**
	 * 读取配置信息
	 * 
	 * @param prop
	 *            ：配置信息
	 */

	public void load_config(Properties prop) {
		use_seg_server = Boolean.parseBoolean(prop
				.getProperty("use_seg_server"));
		// System.out.println("use_seg_server:"+use_seg_server);
		if (use_seg_server == true) {
			seg_port = Integer.parseInt(prop.getProperty("seg_port"));
			// System.out.println("seg_port:"+seg_port);
			seg_server = prop.getProperty("seg_server");
			// System.out.println("seg_server:"+seg_server);
		} else {

		}
	}

	/**
	 * 对输入内容进行分词操作
	 * 
	 * @param text
	 *            ：输入内容
	 * @return：分词结果
	 * @throws Exception
	 */
	public String seg(String text) throws Exception {

		if (!(tnoe(text))) {
			return "";
		}
		String s = text.trim();
		String seg_s = "";
		if (use_seg_server == true) {
			s = s + "\n";
			System.out.println("输入：" + s);
			String server = seg_server;
			int port = seg_port;
			try {
				Socket socket = new Socket(server, port);
				socket.setSoTimeout(100000);
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				out.write(s.getBytes());
				out.flush();

				byte[] receiveBuf = new byte[10032 * 8];
				in.read(receiveBuf);

				seg_s = new String(receiveBuf);
				socket.close();
			} catch (Exception e) {
				Thread.sleep(1000);
			}
		} else {
			System.out.println("字符串为空！");
		}
		return seg_s;
	}

	/**
	 * 提取分词结果中的词汇
	 * 
	 * @param str
	 *            ：分词结果语句
	 * @return：由提取词汇所组成的数组
	 */

	public String[] pro_seg_result(String str) {

		str = str.trim();
		int str_long = str.length();
		int start_num = 0;
		int end_num = 1;
		int i = 0;
		int j = 1;
		// int index = 0;

		int intarr[] = new int[100];

		while (end_num < str_long + 1) {
			String sub_str = str.substring(start_num, end_num);
			if (sub_str.equals(" ")) {
				intarr[i] = end_num;
				i++;
			}
			start_num++;
			end_num++;
		}
		// System.out.println(str.length()+"i的值："+i);
		// for(int num=0; num<i;num++){
		// System.out.println(intarr[num]);
		// }

		String strarr[] = new String[i + 1];
		String result_arr[] = new String[100];
		if (i > 0) {
			strarr[0] = str.substring(0, intarr[0]).trim();
			for (int index = 0; index < i - 1; index++) {
				strarr[j] = str.substring(intarr[index], intarr[index + 1])
						.trim();
				j++;
			}
			strarr[j] = str.substring(intarr[i - 1], str_long).trim();
			if (i == 1) {
				result_arr[0] = strarr[0].trim();
				result_arr[1] = strarr[1].trim();
				result_arr[2] = (strarr[0] + strarr[1]).trim();
			}
		
				for (int num1 = 0; num1 < i + 1; num1++)
					result_arr[num1] = strarr[num1];
				for (int num2 = 0; num2 < i; num2++)
					result_arr[(i + 1) + num2] = (strarr[num2] + strarr[num2 + 1])
							.trim();
				for (int num3 = 0; num3 < i - 1; num3++)
					result_arr[2 * i + 1 + num3] = (strarr[num3]
							+ strarr[num3 + 1] + strarr[num3 + 2]).trim();
			
		

		}

		if (i == 0) {
			result_arr[0] = str.trim();

		}
		return result_arr;
	}

	public static void main(String[] args) throws Exception {
		Sentence_Segmentation ss = new Sentence_Segmentation();
		ss.set_config();
		// Properties prop=new Properties();
		// prop.setProperty("seg_server", "192.168.110.182");
		// prop.setProperty("seg_port", "8092");
		// prop.setProperty("use_seg_server", "true");
		ss.load_config(prop);
		String text = "北京交通大学计算机与信息技术学院";
		// System.out.println("输出："+ss.seg(text));
		// String[] arr = new String[20];
		String[] arr = ss.pro_seg_result(ss.seg(text));
		int longth = arr.length;
		// System.out.println("字符串数组长度：" + longth);
		int index = 0;
		for (int i = 0; i < longth; i++)
			if (arr[i] != null) {
				index++;
				System.out.println(arr[i]);
			}
		System.out.println("字符串数组长度:" + index);
	}

}
