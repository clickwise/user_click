package cn.clickwise.admatch;

import java.util.HashMap;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.edcode.MD5Code;

/***匹配title***/
public class MatchTitle {
	
	static Logger logger = LoggerFactory.getLogger(MatchTitle.class);
	
	public void match_title_records(String title,AdmArg pdb,Map<String,Double> adscore)
	{
	   	adscore.clear();
	   	HashMap<String,HashMap<String,Double>> pairhash=new HashMap<String,HashMap<String,Double>>();
	   	String seg_title;
	   	seg_title=pdb.ansjseg.seg(title);
	   	title_hash_deeper(seg_title,pdb,pairhash);
	   	
	   	 	
	}
	
	public void title_hash_deeper(String seg_title,AdmArg pdb,HashMap<String,HashMap<String,Double>> pairhash)
	{
		String[] title_seg_arr=seg_title.split("\\s+");
	    String word="";
	    String ad_invert_prefix="ad:";
        String md5word="";
        String adline="";
        String[] aid_infotype_count_vec=null;
        Map<String,Map<String,Double>> loc_pairhash;
        String[] ad_seg_arr=null;
		
        String adid="";
        String adinfotype="";
        String adcoutn="";
        String adinfocount="";
        
        for(int j=0;j<title_seg_arr.length;j++)
        {
        	word=title_seg_arr[j];
            if(SSO.tioe(word))
            {
            	continue;
            }
        	md5word=MD5Code.Md5(word);
        	md5word=ad_invert_prefix+md5word;
                            		       	
        }	
	}
	
	public void title_flat_scoremap(HashMap<String,Double> ad_score_map,HashMap<String,Double> mapped_ad_score_map,double mulfactor)
	{
		
	}
	
	public void title_reduce_deeper(AdmArg pdb,HashMap<String,HashMap<String,Double>> pairhash,HashMap<String,Double> adhash)
	{
		
	}
	
	public void title_reduce_one_key(HashMap<String,HashMap<String,Double>> doukeyhash,HashMap<String,Double> onekeyhash)
	{
		
	}
	
}
