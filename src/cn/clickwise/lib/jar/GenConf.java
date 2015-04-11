package cn.clickwise.lib.jar;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class GenConf {

	public static void genlibsconf(String lib_dir, String output_file) {
		File dir = new File(lib_dir);
		File[] sub_files = dir.listFiles();
		File sub_file = null;
		String sub_name = "";

		try {
			
		    PrintWriter pw=new PrintWriter(new FileWriter(output_file));	
			for (int i = 0; i < sub_files.length; i++) {
				
				sub_file = sub_files[i];
				if (sub_file.isDirectory()) {
					continue;
				}
				sub_name = sub_files[i].getName();
				if (!(sub_name.endsWith("jar"))) {
					continue;
				}
                pw.println(genItem(sub_name));
				//System.out.println(sub_name);
				
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String genItem(String jar_name) {
		String conf_str = "";
		conf_str = conf_str + "<unzip src=\"${lib.dir}/";
		conf_str = conf_str + jar_name + "\" dest=\"${classes.dir}\">\n";
		conf_str = conf_str + "<patternset>\n";
		conf_str = conf_str + "<exclude name=\"META-INF\"/>\n";
		conf_str = conf_str + "<exclude name=\"META-INF/MANIFEST.MF\"/>\n";
		conf_str = conf_str + "</patternset>\n";
		conf_str = conf_str + "</unzip>\n";

		return conf_str;
	}

	public static void main(String[] args) {
		String jar = "poi-3.8-20120326.jar";
		// System.out.println(genItem(jar));
		genlibsconf("D:/projects/user_click_win_workplace/HBaseProject/hbase_std_lib", "conf_list_info");
	}
}
