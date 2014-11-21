package cn.clickwise.clickad.profile;

import java.util.Map;

public abstract class ConfigureFactory {

     public abstract Map<String,String> getVariousMapFile();
	
 	 public abstract Profile profileFromStatistic(Map<String, Integer> possibles);

}
