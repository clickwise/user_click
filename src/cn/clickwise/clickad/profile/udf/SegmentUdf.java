package cn.clickwise.clickad.profile.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

public class SegmentUdf extends UDF {
	
    public String evaluate(String str) {

        try {

            return "HelloWorld " + str;

        } catch (Exception e) {

        	
            return null;
        }

    }
    
}
