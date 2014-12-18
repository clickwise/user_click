package cn.clickwise.bigdata.preprocess.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.clickwise.bigdata.util.RadiusClient;

/**
 * 
 * @author gao
 *
 */

public class NstatDataSource extends BaseDataSource{
	private String cookie="";
	private String radius_host_port = "";
	private RadiusClient radius_client = null;
	
	public NstatDataSource(String cookie,String radius_host_port) {
		super();
		init();
		this.cookie=cookie;
		this.radius_host_port = radius_host_port;
		if(cookie.equals("radius") && radius_host_port.length()>0)
			radius_client = new RadiusClient(radius_host_port);
	}
	
	public void init(){
		
		fld_sep_regex="\001";
		
		//初始化字段序号映射表：数据源字段序号=>标准字段序号
		fldMap.put(0, UFLD_AREA); // 0-area
		fldMap.put(1, UFLD_TIME); // 1-time
		fldMap.put(2, UFLD_DATA_TYPE); // 2-data_type
		fldMap.put(3, UFLD_SIP); // 3-sip
		fldMap.put(4, UFLD_DIP); // 4-dip
		fldMap.put(5, UFLD_HOST); // 5-host
		fldMap.put(6, UFLD_URL); // 6-url
		fldMap.put(7, UFLD_REFER); // 7-refer
		fldMap.put(8, UFLD_DOMAIN_ID); // 8-host_cookie
		fldMap.put(9, UFLD_USER_LOC); // 9-ip_loc
		fldMap.put(10, UFLD_USER_AGENT); // 10-agent
		fldMap.put(11, UFLD_USER_ID); // 11-user_id
	}

	/**
	 * 从url中取出uid 或者从radius中取userid
	 */
	@Override
	protected String fld_val(int index, String val, String[] flds) {
		if(cookie.equals("cookie")){
			if(index==6){
				String uid_regex=".*(?i)uid=\\{{0,1}(.*?)}{0,1}(&.*|$)"; //忽略大小写匹配uid=至以&结尾或到字符串末尾
				Pattern pat = Pattern.compile(uid_regex);
				Matcher mat = pat.matcher(flds[6]);
				if(mat.matches()){
					if("".equals(mat.group(1))){
						uniformVals.put(4, "NA");
					}else{
						uniformVals.put(4, mat.group(1));
					}
				}
			}
		}else if(cookie.equals("radius")){
			String radiusUserId=radius_client.getRadiusUserID(flds[1], flds[3]);
			uniformVals.put(4, radiusUserId);
		}
		
		return super.fld_val(index, val, flds);
	}
	
	@Override
	public String process_oneline(String line) {
		return super.process_oneline(line);
	}
	
	public static void main(String[] args) {
		String str="logger.php?ltype=adaction&uiD=11111sdfsd123";
		
		String regex1=".*(?i)uid=\\{{0,1}(.*?)}{0,1}(&.*|$)";
		Pattern pat1 = Pattern.compile(regex1);
		Matcher mat1 = pat1.matcher(str);
		System.out.println(mat1.matches());
		
		System.out.println("a"+mat1.group(1));
		if(mat1.group(1).equals("")){
			System.out.println("aaa");
		}

		
		
	}
	
}
