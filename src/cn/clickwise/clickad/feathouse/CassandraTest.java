package cn.clickwise.clickad.feathouse;

import java.util.List;

import cn.clickwise.lib.code.MD5Code;
import cn.clickwise.lib.time.TimeOpera;

public class CassandraTest {

	public static void main(String[] args)
	{
		if (args.length != 1) {
			System.err.println("Usage:[host]");
			System.exit(1);
		}

		CassandraQuery cq = new CassandraQuery();
		Connection con = new Connection();
		con.setHost(args[0]);
		con.setPort(9160);
		con.setCfName("Urls");
		con.setKeySpace("urlstore");
		con.setColumnName("title");
		cq.connect(con);
		
		Key key=new Key(MD5Code.Md5("niu.xunlei.com/clientembed/xl7mbgame.html?advNo=201402277387966001"));
		long start=TimeOpera.getCurrentTimeLong();
	    List<Record> result=cq.queryUid(key);
	    long end=TimeOpera.getCurrentTimeLong();
	    System.out.println("Use time:"+(end-start)+" ms");
	    
	    for(int i=0;i<result.size();i++)
	    {
	    	System.out.println(result.get(i).toString());
	    }
	}
}
