package cn.clickwise.clickad.feathouse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.clickwise.lib.string.SSO;
import cn.clickwise.lib.time.TimeOpera;


/**
 * 不同地区uid(cookie)查询数量写入mysql
 * @author zkyz
 */
public class Mysql {

	private Connection con;
	private Statement stmt;

	ConfigureFactory confFactory;

	public Mysql() {
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
		connect();
	}

	public void connect() {

		MysqlConfigure myconfig = confFactory.getMysqlConfigure();

		con = null;
		stmt = null;

		String driver = "com.mysql.jdbc.Driver";
		String url;
		if (SSO.tnoe(myconfig.getDbname())) {
			url = "jdbc:mysql://" + myconfig.getIp() + ":" + myconfig.getPort()
					+ "/" + myconfig.getDbname();
		} else {
			url = "jdbc:mysql://" + myconfig.getIp() + ":" + myconfig.getPort()
					+ "/";
		}
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, myconfig.getUser(),
					myconfig.getPassword());
			stmt = con.createStatement();

		} catch (ClassNotFoundException e1) {
			System.out.println("数据库驱动不存在！");
			System.out.println(e1.toString());
		} catch (SQLException e2) {
			System.out.println("数据库存在异常！");
			System.out.println(e2.toString());
		} finally {
		
				//if (stmt != null)
				//	stmt.close();
				//if (con != null)
					//con.close();
		
		}
	}

	public State insertStatistics(Receipt receipt,Table table) {
		
		State state = new State();
        String sql = "insert into  "+table.getName()+"(area_code,area,date,pv,uv) values("+receipt.getDmp().getArea().getAreaCode()+",'"+receipt.getDmp().getArea().getName()+"',"+receipt.getDay()+","+receipt.getPv()+","+receipt.getUv()+");";
        System.out.println("sql:"+sql);
        try{
          stmt.executeUpdate(sql);
          state.setStatValue(StateValue.Normal);
        }
        catch(SQLException e)
        {
          e.printStackTrace();	
          state.setStatValue(StateValue.Error);
        }
        
		return state;
	}
	
	
	public static void main(String[] args)
	{
		Dmp dmp=new Dmp();
		dmp.setName("186");
		dmp.setArea(new Area("local","188"));
		dmp.setHost("192.168.110.186");
		dmp.setRpcPort(2733);
		dmp.setDmpInquiryMethod("/hiveFetchByKeys");
		dmp.setUserFeatureTableName("user_se_keywords_day_ad");
		dmp.setUidFieldName("cookie");
		dmp.setTmpIdentify("remote_cookie");
		
		InquiryReceipt receipt=new InquiryReceipt();
		receipt.setDay(TimeOpera.getToday());
		receipt.setDmp(dmp);
		receipt.setUv(200);
		receipt.setPv(100);
		receipt.setReceiptId(System.currentTimeMillis()+"");
		
		ConfigureFactory confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
		Table inquiryTable=confFactory.getInquiryTable();
		Mysql mysql=new Mysql();
		mysql.insertStatistics(receipt, inquiryTable);		
	
		
		
	}
	
	
	

}
