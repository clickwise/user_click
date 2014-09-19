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
	private String statisticsTableName;

	public Mysql() {
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
		connect();
		statisticsTableName=confFactory.getStatisticsTableName();
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
			try {
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
		}
	}

	public State insertStatisticsRecord(StatisticsRecord statrec) {
		
		State state = new State();
        String sql = "insert into "+statisticsTableName+" values ('"+statrec.getArea()+"',"+TimeOpera.formatDay(statrec.getDate())+","+statrec.getUv()+","+statrec.getPv()+")";
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

}
