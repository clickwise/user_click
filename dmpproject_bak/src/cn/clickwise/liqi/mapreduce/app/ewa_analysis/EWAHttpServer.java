package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import com.sun.net.httpserver.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hbase.util.Base64;

public class EWAHttpServer {
	public EWAPredict ewa;

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("用法 :EWAECServer <port>");
		}
		int port = 8900;
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			port = 8900;
		}
		EWAHttpServer ewa_se = new EWAHttpServer();
		String model_path = "model_dir/model";
		String sls_path = "model_dir/lll.txt";
		String first_level_path = "model_dir/fhc.txt";
		String second_level_path = "model_dir/shc.txt";
		String third_level_path = "model_dir/thc.txt";
		String cw_file = "input/ec_ckws_num.txt";
		ewa_se.ewa = new EWAPredict();
		ewa_se.ewa.read_model(model_path, sls_path, first_level_path,
				second_level_path, third_level_path);
		ewa_se.ewa.load_config();
		ewa_se.ewa.load_cate_wrods(cw_file);

		try {
			HttpServer hs = HttpServer.create(new InetSocketAddress(port), 0);
			MyHandler mh = new MyHandler();
			mh.setEwa(ewa_se.ewa);
			hs.createContext("/cate_tb", mh);
			hs.setExecutor(null);
			hs.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class MyHandler implements HttpHandler {
	public EWAPredict my_ewa;

	public void setEwa(EWAPredict temp_ewa) {
		this.my_ewa = temp_ewa;
	}

	public void handle(HttpExchange t) throws IOException {
		// System.out.println(t.getRequestURI().toString());
		/*
		 * InputStream is = t.getRequestBody(); byte[] temp = new
		 * byte[is.available()]; is.read(temp); String title=new String(temp);
		 */
		String title_str = t.getRequestURI().toString();
		//System.out.println("title_str:" + title_str);
		/*
		 * Pattern tit_pat=Pattern.compile("\\/cate_db\\/do\\?t\\=(.*?)\\=");
		 * Matcher tit_mat=tit_pat.matcher(title_str); String tt="";
		 * while(tit_mat.find()) { tt=tit_mat.group(1);
		 * System.out.println("title:"+tt); }
		 */
		title_str = title_str.replaceFirst("/cate_tb/do\\?t=", "");
		//System.out.println("title_str:" + title_str);
		String de_title = new String(Base64.decode(title_str));
		System.out.println("de_title:" + de_title);
		String res = "";
		try {
			res = my_ewa.predict_from_seg_line(de_title);
		} catch (Exception e) {
		}
		;
		System.out.println("res is :" + res);
		// res=res.replace("\001", "$");
		String encode_res = Base64.encodeBytes(res.getBytes());
		encode_res = encode_res.replaceAll("\\s+", "");
		System.out.println("encode_res:" + encode_res);

		String response = encode_res;
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
