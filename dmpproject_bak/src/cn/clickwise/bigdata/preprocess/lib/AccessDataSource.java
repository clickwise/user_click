package cn.clickwise.bigdata.preprocess.lib;

import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.clickwise.bigdata.util.iploc.IP2long;
import cn.clickwise.bigdata.util.iploc.IPSeeker;

/**
 * 
 * @author gao
 * 
 */
public class AccessDataSource extends BaseDataSource {
	
	public AccessDataSource() {
		super();
		init();
	}

	public void init() {
		fld_sep_regex = "\001";
		
		fldMap.put(0, UFLD_TIME); // 0-time
		fldMap.put(1, UFLD_DOMAIN_ID); // 1-domain
		fldMap.put(2, UFLD_PROXY_IP); // 2-ip
		fldMap.put(3, UFLD_AREA); // 3-area
		fldMap.put(4, UFLD_TITLE); // 4-title
		fldMap.put(5, UFLD_REFER); // 5-refer
		fldMap.put(6, UFLD_SIP); // 6-ci_ip
		fldMap.put(7, UFLD_USER_LOC); // 7-ci_ip_area
		fldMap.put(8, UFLD_USER_ID); // 8-cookie
		fldMap.put(9, UFLD_HOST); // 9-host
		fldMap.put(10, UFLD_URL); // 10-url
		fldMap.put(11, UFLD_USER_AGENT); // 11-agent
		fldMap.put(12, UFLD_DATA_TYPE); // 12-data_type
	}
	
	
	

	@Override
	protected String fld_val(int index, String val, String[] flds) {
		// TODO Auto-generated method stub
		return super.fld_val(index, val, flds);
	}

	@Override
	public String process_oneline(String line) {
		return super.process_oneline(formatLine(line));
	}

	/**
	 *正则表达式匹配log文件中的字段
	 * @param one_line
	 * @return
	 */

	public HashMap<String, String> format_log_line(String one_line) {
		HashMap<String, String> line_Map = new HashMap<String, String>();
		try {
			String regex = "^(\\S+) (\\S+) (\\S+) \\[([^:]+):(\\d+:\\d+:\\d+) ([^\\]]+)\\] \"(\\S+) (.*?) (\\S+)\" (\\S+) (\\S+) (\".*?\") (\".*?\") (\".*?\")$";
			Pattern pat = Pattern.compile(regex);
			Matcher mat = pat.matcher(one_line);
			while (mat.find()) {
				line_Map.put("ip", mat.group(1));
				line_Map.put("identity", mat.group(2));
				line_Map.put("user", mat.group(2));
				line_Map.put("date", mat.group(4));
				line_Map.put("time", mat.group(5));
				line_Map.put("timezone", mat.group(6));
				line_Map.put("method", mat.group(7));
				line_Map.put("path", mat.group(8));
				line_Map.put("protocal", mat.group(9));
				line_Map.put("status", mat.group(10));
				line_Map.put("bytes", mat.group(11));
				line_Map.put("referer", mat.group(12));
				line_Map.put("agent", mat.group(13));
				line_Map.put("cookie", mat.group(14));
			}
			
		} catch (Exception e) {
			
		}
		
		return line_Map;
	}
/**
 * 对format_log_line（）匹配出的字段进一步处理
 * @param line
 */
	public String formatLine(String line) {
		String input_ip="NA";
		String input_identity="NA";
		String input_url="NA";
		String input_agent="NA";
		String input_time="NA";
		String input_path="";
		String input_host="NA";
		String input_cookie="NA";
		
		StringBuilder sb=new StringBuilder();
		try {
			HashMap<String,String> map=format_log_line(line);
				input_ip=map.get("ip");
				input_identity=map.get("identity");
				input_url=map.get("referer");
				input_agent=map.get("agent");
				
				input_time=dateFormat(map.get("date"))+" "+map.get("time");
				input_path=pathFormat(map.get("path"));
					
				URL url=new URL(map.get("referer"));
				input_host=url.getHost();
				
				String regex1="\"(?i)uaid=(.*)\"";
				Pattern pat1 = Pattern.compile(regex1);
				Matcher mat1 = pat1.matcher(map.get("cookie"));
				if(mat1.matches()){
					input_cookie=mat1.group(1);
				}
			
		} catch (Exception e) {
			
		}
		
		sb.append(input_time+"\001");
		sb.append(input_identity+"\001");
		sb.append(input_ip+"\001");
		sb.append(input_path);
		sb.append(input_cookie+"\001");
		sb.append(input_host+"\001");
		sb.append(input_url+"\001");
		sb.append(input_agent+"\001");
		sb.append("IACCESS");
		
		return sb.toString();

	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	
	public String pathFormat(String path){
		String input_area="NA";
		String input_title="NA";
		String input_refer="NA";
		String input_ci_ip="NA";
		String input_ci_ip_area="NA";
		StringBuilder sb=new StringBuilder();
		
		String regex="/re/re.php\\?(.*)";
		Pattern pat= Pattern.compile(regex);
		Matcher mat = pat.matcher(path);
		if(mat.matches()){
			
			String[] str=mat.group(1).split("&");
			for(String s:str){
				try {
				if(s.contains("src=")){
					input_area=s.split("=")[1];
				}
				if(s.contains("t=")){
					input_title=s.split("=")[1];
				}
				if(s.contains("r=")){
					input_refer=s.split("=")[1];
				}
				if(s.contains("ci=")){
					input_ci_ip=IP2long.long2ip(IP2long.ip2long(s.split("=")[1]));
					IPSeeker ips = IPSeeker.getInstance();
					input_ci_ip_area=ips.getAddress(input_ci_ip);
				}
			} catch (Exception e) {
				continue;
			}
		}
	}
		sb.append(input_area+"\001");
		sb.append(input_title+"\001");
		sb.append(input_refer+"\001");
		sb.append(input_ci_ip+"\001");
		sb.append(input_ci_ip_area+"\001");
		
		return sb.toString();
	}
	
	/**
	 * 将日期装换成统一格式 例：2014-04-25 11:00:50
	 * @param date
	 * @return
	 */
	public String dateFormat(String date){
		
		String date_array[]=date.split("/");
		String formated_date=date_array[2]+"-"+Month.valueOf(date_array[1]).month+"-"+date_array[0];
		return formated_date;
	}
	
	/**
	 * 枚举类型 月份的简写 以及对应的数字，用于date的格式化
	 * @author gao
	 *
	 */
	enum Month{
		Jan("01"),
		Feb("02"),
		Mar("03"),
		Apr("04"),
		May("05"),
		Jun("06"),
		Jul("07"),
		Aug("08"),
		Sep("09"),
		Oct("10"),
		Nov("11"),
		Dec("12");
		private String month;
		private Month(String month){
			this.month=month;
		}
	}

	
	public static void main(String[] args) {

	}
	

}
