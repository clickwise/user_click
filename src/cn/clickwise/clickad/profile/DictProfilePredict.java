package cn.clickwise.clickad.profile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;


import cn.clickwise.lib.string.SSO;

import love.cq.util.MapCount;

public class DictProfilePredict extends ProfilePredict {

	public ConfigureFactory confFactory;

	public void loadOneDict(String key, String file) {

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
            System.out.println("key:"+key+" mc:"+mc.get().size());
			variousMapDict.put(key, mc);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void loadKnowledge() {
		confFactory=ConfigureFactoryInstantiate.getConfigureFactory();
		for (Map.Entry<String, String> m : confFactory.getVariousMapFile()
				.entrySet()) {
			loadOneDict(m.getKey(), m.getValue());
		}

	}

	@Override
	public Profile predict(User user) {

		Map<String, Integer> possibles = new HashMap<String, Integer>();
		for (Map.Entry<String, MapCount<String>> m : variousMapDict.entrySet()) {
			possibles.put(m.getKey(), 0);
		}

		System.err.println("predict user:"+user.getKeyText());
		String[] words = user.getKeyText().split("\\s+");
		String word = "";

		for (int i = 0; i < words.length; i++) {
			
			word = words[i];
			if (SSO.tioe(word)) {
				continue;
			}

			for (Map.Entry<String, MapCount<String>> m : variousMapDict.entrySet()) {
				
				if((m.getValue().get()).containsKey(word))
				{
					possibles.put(m.getKey(), possibles.get(word)+1);
				}
				else
				{
					possibles.put(m.getKey(), 1);
				}
			}
			
		}
		
		return confFactory.profileFromStatistic(possibles);
	}


}
