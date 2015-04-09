package cn.clickwise.clickad.hbase;

import java.util.List;

/**
 * 根据ip+time+status查询
 * rowkey: IP+时间+状态+md5(账号)
 * cf:column 为 orid:旧帐号
 *
 * @author zkyz
 *
 */
public class ITSRadiusStore extends RadiusStore{

	@Override
	public void write(String record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> get(String ip, String time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> get(String ip) {
		// TODO Auto-generated method stub
		return null;
	}

}
