package cn.clickwise.clickad.jd_opinion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import cn.clickwise.lib.string.SSO;

/**
 * 解析搜索连接，提取有用的信息
 * 
 * @author zkyz
 */
public class SEUrlParse {

	public static ParseResult parseItem(String line) {

		String host = "";
		String url = "";
		String refer = "";

		ParseResult pr = new ParseResult();
		String[] fields = line.split("\001");
		if (fields.length < 11) {
			return pr;
		}

		host = fields[5];
		url = fields[6];
		refer = fields[7];

		if ((SSO.tioe(url)) && (SSO.tioe(refer))) {
			return pr;
		}

		if (host.indexOf("m.baidu.com") < 0) {
			return pr;
		}

		String uwword = SSO.midfstrs(url, "wd=", "&");
		String uuword = SSO.midfstrs(url, "word=", "&");
		String uqword = SSO.midfstrs(url, "query=", "&");

		String rwword = SSO.midfstrs(refer, "wd=", "&");
		String ruword = SSO.midfstrs(refer, "word=", "&");
		String rqword = SSO.midfstrs(refer, "query=", "&");

		// System.err.println("uwword:"+uwword+" uuword:"+uuword+" uqword:"+uqword+" rwword:"+rwword+" ruword:"+ruword+" rqword:"+rqword);

		if (SSO.tnoe(uwword) && uwword.indexOf("%") >= 0
				&& uwword.indexOf("http") < 0) {
			pr.setUwword(uwword);
		}

		if (SSO.tnoe(uuword) && uuword.indexOf("%") >= 0
				&& uuword.indexOf("http") < 0) {
			pr.setUuword(uuword);
		}

		if (SSO.tnoe(uqword) && uqword.indexOf("%") >= 0
				&& uqword.indexOf("http") < 0) {
			pr.setUqword(uqword);
		}

		if (SSO.tnoe(rwword) && rwword.indexOf("%") >= 0
				&& rwword.indexOf("http") < 0) {
			pr.setRwword(rwword);
		}

		if (SSO.tnoe(ruword) && ruword.indexOf("%") >= 0
				&& ruword.indexOf("http") < 0) {
			pr.setRuword(ruword);
		}

		if (SSO.tnoe(rqword) && rqword.indexOf("%") >= 0
				&& rqword.indexOf("http") < 0) {
			pr.setRqword(rqword);
		}

		return pr;
	}

	public static void parseStd() {

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		try {

			// BufferedReader br = new BufferedReader(new
			// FileReader("temp/201503302350_t6401_eth2_1716560057.log"));

			OutputStreamWriter osw = new OutputStreamWriter(System.out);
			PrintWriter pw = new PrintWriter(osw);

			String line = "";
			ParseResult pr = null;

			while ((line = br.readLine()) != null) {
				try {
					pr = parseItem(line);
					if (pr.isNull()) {
						continue;
					}
					pr.decode();

					if (pr.isInValid()) {
						continue;
					}
					pw.println(pr.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			br.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		SEUrlParse.parseStd();
	}

}
