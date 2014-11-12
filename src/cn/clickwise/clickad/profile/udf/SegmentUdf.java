package cn.clickwise.clickad.profile.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.jmlp.str.app.SetJar;

import cn.clickwise.lib.string.SSO;

public class SegmentUdf extends UDF {
	
	private static final SetJar setJar=new SetJar();
	
    public String evaluate(String str) {

        try {
        	if(SSO.tioe(str))
        	{
        		return "";
        	}
        	
            String seg_str=setJar.double_seg(str);
            if(SSO.tioe(seg_str))
            {
            	return "";
            }
            
            return seg_str;

        } catch (Exception e) {
        }
        return "";

    }
    
}
