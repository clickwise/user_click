package cn.clickwise.clickad.profile.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

import cn.clickwise.lib.string.SSO;

public class TestUdf extends UDF{

	public String evaluate(String str) {

		return str;
	}
}
