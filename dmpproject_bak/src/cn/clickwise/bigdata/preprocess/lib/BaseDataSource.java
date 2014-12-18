package cn.clickwise.bigdata.preprocess.lib;

import java.util.HashMap;

/**
 * Base Class for DataSource Unify
 * 
 * @author gao
 *
 */
public class BaseDataSource {
	public static HashMap< Integer,String> uniformVals = new HashMap<Integer,String>();//统一格式字段对应
	public static String fld_sep_regex=""; //字段分割正则表达式
	public static HashMap<Integer, Integer> fldMap=new HashMap<Integer, Integer>(); //数据字段映射表: 数据源字段序号->标准字段序号
	
	//定义标准字段顺序
	public final static int UFLD_TIME=0; //time, 0
	public final static int UFLD_DOMAIN_ID =1; // domain_id ,1
	public final static int UFLD_PROXY_IP =2;//"proxy_ip", 2
	public final static int UFLD_AREA =3;//"area", 3
	public final static int UFLD_USER_ID =4;//"user_id", 4
	public final static int UFLD_USER_DOMAIN =5;//"user_domain", 5
	public final static int UFLD_HOST =6;//"host", 6
	public final static int UFLD_URL =7;//"url", 7
	public final static int UFLD_TITLE =8;//"title", 8
	public final static int UFLD_USER_AGENT =9;//"user_agent", 9
	public final static int UFLD_SIP =10;//"sip", 10
	public final static int UFLD_DIP =11;//"dip", 11
	public final static int UFLD_USER_LOC = 12;//"user_loc", 12
	public final static int UFLD_REFER = 13;//"refer", 13
	public final static int UFLD_DATA_TYPE =14;//"data_type", 14
	public final static int UFLD_OF1 =15;//"Of1", 15
	public final static int UFLD_OF2 =16;//"Of2", 16
	public final static int UFLD_OF3 =17;//"Of3", 17
	public final static int UFLD_OF4 =18;//"Of4", 18
	
	public final static int UFLD_START = 0;// 起始序号
	public final static int UFLD_END =UFLD_OF4+1;// 结束序号
	
	public BaseDataSource(){
		//Initialize to NA
		for(int i = UFLD_START ; i < UFLD_END; i++){
			uniformVals.put(i, "NA");
		}
	}
/**
 * 子类可重写此方法，完成对自身特殊字段的处理
 * @param index
 * @param val
 * @param flds
 * @return
 */
	protected String fld_val(int index, String val, String[] flds){
		return val;
	}
	/**
	 * 数据源字段和标准字段的直接映射
	 * 
	 * @param line	数据源的一行输入
	 * @param fld_sep_regex	数据源字段的分隔字符
	 * @param fldMap	数据源字段和标准字段的序号映射Map
	 * @return 标准化之后的数据结果。如果数据格式非法，返回null
	 */
	private String uniform_mapping(String line, String fld_sep_regex, HashMap<Integer,Integer> fldMap){
		String one_line =line.trim();
		String[]flds=one_line.split(fld_sep_regex);
		if(flds == null || flds.length <5)
			return null;
		
		for(int i=0;i<flds.length;i++){
			uniformVals.put(fldMap.get(i), fld_val(i,flds[i],flds));
		}
		
		//Construct returned string
		StringBuilder sb = new StringBuilder();
		for(int i = UFLD_START; i < UFLD_END; i++){
			if( i != UFLD_START)
				sb.append('\001');
			sb.append(uniformVals.get(i));
		}
		
		return sb.toString();
	}
	
	/**
	 * 处理一行数据源输入
	 * 	派生类可以完全重载此函数，
	 *  或者填写合适的字段分割正则表达式(fld_sep_regex)以及字段序号映射表完成功能。
	 * 
	 * @param line	输入数据源的一行数据
	 * @return 标准化之后的处理结果
	 */
	public String process_oneline(String line) {
		return uniform_mapping(line,fld_sep_regex,fldMap);
	}
}
