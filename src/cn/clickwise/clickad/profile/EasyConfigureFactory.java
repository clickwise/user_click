package cn.clickwise.clickad.profile;

import java.util.HashMap;
import java.util.Map;

public class EasyConfigureFactory extends ConfigureFactory{

	@Override
	public Map<String, String> getVariousMapFile() {
		
		Map<String,String> variousMap=new HashMap<String,String>();
		variousMap.put("性别->男性", "mrjobs/gender/result/nan_dict_sort.txt");
		variousMap.put("性别->女性", "mrjobs/gender/result/nv_dict_sort.txt");
		variousMap.put("年龄->中小学生", "mrjobs/age/result/zxxs.txt");
		variousMap.put("年龄->大学研究生", "mrjobs/age/result/daxue_dict_m.txt");
		variousMap.put("年龄->中年", "mrjobs/age/result/zhongnian_dict.txt");
		variousMap.put("年龄->老年", "mrjobs/age/result/laonian_idf_sort.txt");
		variousMap.put("收入->高收入", "mrjobs/income/result/rich_dict.txt");
		variousMap.put("收入->低收入", "mrjobs/income/result/unrich_dict_u.txt");
		
		return variousMap;
	}

	@Override
	public Profile profileFromStatistic(Map<String, Integer> possibles) {

        Profile pro=new Profile();
        if(possibles.get("性别->男性")>possibles.get("性别->女性"))
        {
        	pro.setGender("男性");
        }
        else
        {
        	pro.setGender("女性");
        }
        
        int maxCount=-1;
        String maxAge="";
        
        if(possibles.get("年龄->中小学生")>maxCount)
        {
        	maxCount=possibles.get("年龄->中小学生");
        	maxAge="年龄->中小学生";
        }
        
        if(possibles.get("年龄->大学研究生")>maxCount)
        {
        	maxCount=possibles.get("年龄->大学研究生");
        	maxAge="年龄->大学研究生";
        }
        
        if(possibles.get("年龄->中年")>maxCount)
        {
        	maxCount=possibles.get("年龄->中年");
        	maxAge="年龄->中年";
        }
        
        if(possibles.get("年龄->老年")>maxCount)
        {
        	maxCount=possibles.get("年龄->老年");
        	maxAge="年龄->老年";
        }
        
        pro.setAge(maxAge);
        
        if(possibles.get("收入->高收入")>possibles.get("收入->低收入"))
        {
        	pro.setIncome("收入->高收入");
        }
        else
        {
        	pro.setIncome("收入->低收入");
        }
        
		return pro;
	}



}
