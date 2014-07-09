package cn.clickwise.liqi.file.uitls;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import cn.clickwise.liqi.str.basic.SSO;

public class TrimField {

	public void trimField() {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String line = "";
		String[] seg_arr = null;

		String trimline = "";
		try {
			while ((line = br.readLine()) != null) {
				if (SSO.tioe(line)) {
					continue;
				}

				seg_arr = line.split("\001");
				if (seg_arr.length < 1) {
					continue;
				}

				trimline = "";
				for (int j = 0; j < seg_arr.length; j++) {
					trimline += seg_arr[j].trim()+"\001";
				}
                trimline=trimline.trim();
				pw.println(trimline);

			}

			isr.close();
			osw.close();
			br.close();
			pw.close();
		} catch (Exception e) {

		}

	}

	public static void main(String[] args) {
		TrimField tf=new TrimField();
		tf.trimField();
	}
}
