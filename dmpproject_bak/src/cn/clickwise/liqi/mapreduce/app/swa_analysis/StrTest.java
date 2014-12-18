package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrTest {

	public String trim_small_words(String sw_info) {
		String tsw_info = "";
        String look_four_str="";
        String look_three_str="";
        String en_num_str="";
        sw_info=sw_info.trim();
        if((sw_info==null)||(sw_info.equals("")))
        {
        	return "";
        }

        String[] seg_arr=sw_info.split("\\s+");
        String temp_word="";
		if(seg_arr==null)
		{
			return "";
		}
		
		Vector sel_word_vec=new Vector();
		for(int i=0;i<seg_arr.length;i++)
		{
			temp_word=seg_arr[i].trim();
	        if((temp_word==null)||(temp_word.equals("")))
	        {
	        	continue;
	        }
	        if(temp_word.length()==4)
	        {
	        	look_four_str=look_four_str+temp_word+"_";
	        }
	        else if(temp_word.length()==3)
	        {
	        	look_three_str=look_three_str+temp_word+"_";
	        } 
	        
	        if(isEnNumbers(temp_word))
	        {
	        	en_num_str=en_num_str+temp_word+"_";
	        }
		}
		
		for(int i=0;i<seg_arr.length;i++)
		{
			temp_word=seg_arr[i].trim();
            if((temp_word.length())==2) 
            {
            	if(((look_three_str.indexOf(temp_word))==-1)&&((look_four_str.indexOf(temp_word))==-1))
            	{
            		sel_word_vec.add(temp_word);
            	}
            }
            else if((temp_word.length())==3)
            {
            	if((look_four_str.indexOf(temp_word))==-1)
            	{
            		sel_word_vec.add(temp_word);
            	}
            }
            else if(isEnNumbers(temp_word))
            {
            	if((en_num_str.indexOf(temp_word))==-1)
            	{
            		sel_word_vec.add(temp_word);
            	}
            }
            else
            {
            	sel_word_vec.add(temp_word);
            }        
		}
		
		
		for(int i=0;i<sel_word_vec.size();i++)
		{
		   temp_word=sel_word_vec.get(i)+"";
		   tsw_info=tsw_info+temp_word+" ";
		}
		tsw_info=tsw_info.trim();	
		return tsw_info;
	}
	
	
	public boolean isEnNumbers(String s) {
		boolean ian = false;
		String pat = "[0-9\\.\\-\\+A-Za-z]*";
		if (Pattern.matches(pat, s)) {
			ian = true;
		}
		return ian;
	}
	
	public boolean isUrl(String str) {
		boolean isUrl = false;
		if (Pattern.matches("(?:(?:www)|(?:WWW)|[a-zA-Z0-9])[\\.]?[a-z0-9_A-Z\\.\\?\\&\\=]*", str)) {
			isUrl = true;
		} else if (Pattern.matches(
				"http:\\/\\/(?:(?:www)|(?:WWW)|[a-zA-Z0-9])[\\.]?[a-z0-9_A-Z\\.\\/\\?\\&\\=]*", str)) {
			isUrl = true;
		}
		return isUrl;
	}
	
	public boolean isLongEnSearch(String str){
		boolean isles=false;
		String les_reg="[A-Za-z0-9\\.\\,]*\\+[A-Za-z0-9\\.\\,]*\\+[A-Za-z0-9\\.\\,]*\\+[A-Za-z0-9\\.\\,]*";
		Pattern les_pat=Pattern.compile(les_reg);
		Matcher les_mat=les_pat.matcher(str);
		while(les_mat.find())
		{
			isles=true;
		}
		les_reg="[A-Za-z0-9\\.\\,]*\\s+[A-Za-z0-9\\.\\,]*\\s+[A-Za-z0-9\\.\\,]*\\s+[A-Za-z0-9\\.\\,]*";
	    les_pat=Pattern.compile(les_reg);
		les_mat=les_pat.matcher(str);
		while(les_mat.find())
		{
			isles=true;
		}
		
		
		return isles;
	}
	
	public String getDeCodeStr(String code_str) {
		String decode_text = "";
	    decode_text=URLDecoder.decode(code_str);
		return decode_text;
	}
	
	public static void main(String[] args) {
		
		StrTest st=new StrTest();
		/*
		String sw_info = "光之子小说 小说 光之子 光之 之子";
		String trim_sw_info="";
		trim_sw_info=st.trim_small_words(sw_info);
		System.out.println("trim_sw_info: "+trim_sw_info);
		
		String url="光之子小说 小说 光之子";
		url=url.trim();
		System.out.println("isUrl:"+st.isUrl(url));
		
		String les="even+if+its+not+a+joke+its+fine+wid+me+的意思";
		System.out.println("isLES:"+st.isLongEnSearch(les));
		*/
		
		
		String encode_str="%E5%B7%B4%E5%8E%98%E5%B2%9B";		
        System.out.println(st.getDeCodeStr(encode_str));
	}

}
