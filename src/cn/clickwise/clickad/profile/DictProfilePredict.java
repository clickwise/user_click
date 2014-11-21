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

	public Word[] words2Words(String[] words)
	{
		MapCount<String> mc=new MapCount<String>();
		String w="";
		for(int i=0;i<words.length;i++)
		{
			w=words[i];
			if(SSO.tioe(w)){
				continue;
			}
			w=w.trim();
			mc.add(w);
		}
		
		Word[] ws=new Word[mc.get().size()];
		int index=0;
		for(Map.Entry<String, Integer> m:mc.get().entrySet())
		{
		 	ws[index]=new Word(m.getKey(),m.getValue());
		}
	
		return ws;
	}
	@Override
	public Profile predict(User user) {

		Map<String, Integer> possibles = new HashMap<String, Integer>();
		
		for (Map.Entry<String, MapCount<String>> m : variousMapDict.entrySet()) {
			possibles.put(m.getKey(), 0);
		}

		System.err.println("predict user:"+user.getKeyText());
		String[] words = user.getKeyText().split("\\s+");
		//String word = "";

		Word[] ws=words2Words(words);
		Word w=null;
		int oc=0;
		for (int i = 0; i < ws.length; i++) {
			
			w = ws[i];
			if (SSO.tioe(w.getWord())) {
				continue;
			}

			for (Map.Entry<String, MapCount<String>> m : variousMapDict.entrySet()) {
				if(!((m.getValue().get()).containsKey(w.getWord())))
				{
					possibles.put(m.getKey(), 0);
				}
				if((m.getValue().get()).containsKey(w.getWord()))
				{
					System.out.println("mkey:"+m.getKey()+" word:"+w.getWord());
					oc=possibles.get(m.getKey())+w.getValue();
					System.out.println("oc:"+oc);
					possibles.put(m.getKey(), oc);
				}
			}
			
		}
		
		return confFactory.profileFromStatistic(possibles);
	}


}
