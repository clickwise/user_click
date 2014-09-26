package cn.clickwise.lib.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	// 在default path 和 jar包里寻找名称为name的配置文件
	// ，并转换为properties
	public static Properties file2properties(String name) {
		
		Properties prop=new Properties();
		InputStream propis = null;
    	File local = new File(name);

		try {
			if (local.exists()) {
				propis = new FileInputStream(local);
			} else {
				propis = PropertiesUtil.class.getResourceAsStream("/" + name);
			}
			prop.load(propis);
			propis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return prop;
	}
	
}
