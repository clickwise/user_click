package cn.clickwise.bigdata.preprocess.lib;

/**
 * 
 * @author gao
 *
 */
public class RstatDataSource extends BaseDataSource{
	
	public RstatDataSource() {
		super();
		init();
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
		fldMap.put(8, UFLD_USER_ID); // 8-cookies
		fldMap.put(9, UFLD_USER_LOC); // 9-ip_loc
	}

	@Override
	public String process_oneline(String line) {
		// TODO Auto-generated method stub
		return super.process_oneline(line);
	}
	

}
