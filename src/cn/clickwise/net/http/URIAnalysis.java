package cn.clickwise.net.http;

import java.net.URI;

public class URIAnalysis {

	public static String getHost(String url) {
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (Exception e) {

		}

		if (uri == null) {
			return "NA";
		} else {
			return uri.getHost();
		}

	}

}
