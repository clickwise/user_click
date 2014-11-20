package cn.clickwise.clickad.profile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

import cn.clickwise.lib.string.SSO;

import love.cq.util.MapCount;

public class DictProfilePredict extends ProfilePredict {

	public ConfigureFactory confFactory;
	
	public void loadOneDict(String key,String file) {
		
		MapCount<String> mc = new MapCount<String>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			String line = "";

			while ((line = br.readLine()) != null) {
				if (SSO.tioe(line)) {
					continue;
				}
				mc.add(line);
			}
			
			br.close();

			variousMapDict.put(key, mc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	@Override
	public void loadKnowledge() {

		for(Map.Entry<String, String> m:confFactory.getVariousMapFile().entrySet())
		{
			 loadOneDict(m.getKey(),m.getValue());		
		}
		
	}

	@Override
	public Profile predict(User user) {

		return null;
	}

}
