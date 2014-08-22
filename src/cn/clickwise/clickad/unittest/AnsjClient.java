package cn.clickwise.clickad.unittest;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;
import cn.clickwise.liqi.str.edcode.UrlCode;
import cn.clickwise.liqi.time.utils.TimeOpera;

public class AnsjClient extends AuxiliaryTestBase {

	public String method = "/seg?s=";

	
	@Override
	public String test(String text) {
		// System.out.println(text);
		text = URLEncoder.encode(text);
		String url = auxiliary_prefix + method + text;
		 //System.out.println(url);
		String response = hct.postUrl(url);
		return response;
	}
	
	public static void main(String[] args) {

		try {
			AnsjClient ansj = new AnsjClient();
			PrintWriter pw = FileWriterUtil
					.getPW("temp/seg_test/tb_test_seg.txt");
			long start_time = TimeOpera.getCurrentTimeLong();

			String[] unsegs = FileToArray
					.fileToDimArr("temp/seg_test/tb_test3.txt");
			String texts = "";
			for (int i = 0; i < unsegs.length; i++) {
				
				long sub_start_time = TimeOpera.getCurrentTimeLong();
				pw.println(UrlCode.getDecodeUrl(ansj.test(unsegs[i])));
				long sub_end_time = TimeOpera.getCurrentTimeLong();			
				System.out.println("rec "+i+" use time:"
						+ ((double) (sub_end_time - sub_start_time) / (double) 1000)
						+ " seconds");
			}

			long end_time = TimeOpera.getCurrentTimeLong();

			System.out.println(unsegs.length + " total doc, use time:"
					+ ((double) (end_time - start_time) / (double) 1000)
					+ " seconds");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String test(String[] text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> testmul(String[] text) {
		// TODO Auto-generated method stub
		return null;
	}



}
