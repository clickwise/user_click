package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import redis.clients.jedis.Jedis;

/**
 * 判断是否是符合要求的词，并将选出的词存入redis词典 
 * @author lq
 *
 */

public class SWADetectWord {

	public Jedis swa_small_dict_redis;
	public String swa_small_dict_ip;
	public int swa_small_dict_port;
	public int swa_small_dict_db;
	public SWASegDict swa_seg = null; 
	public SWATagNoun swa_tag = null;
	public Hashtable<String,String> wci_hash=null;
	public void load_local_config()
	{		
		try{
		InetAddress addr = InetAddress.getLocalHost();
        String ip=addr.getHostAddress().toString();//获得本机IP
        String address=addr.getHostName().toString();//获得本机名
        
        swa_small_dict_port=6379;
        swa_small_dict_db=2;
 
        if(address.equals("adt1"))
        {
        	swa_small_dict_ip="192.168.110.181";
        }
        else if(address.equals("hndx_fx_100"))
		{
        	swa_small_dict_ip="192.168.1.100";
		}
	
        
        swa_small_dict_redis = new Jedis(swa_small_dict_ip,  swa_small_dict_port, 100000);// redis服务器地址
        swa_small_dict_redis.ping();
        swa_small_dict_redis.select(swa_small_dict_db);
        
             
        swa_seg = new SWASegDict(); 
        swa_seg.load_local_config();
		
		swa_tag = new SWATagNoun();
		swa_tag.load_local_config();
		}
		catch(Exception e)
		{
			System.out.println("in swa_detec_word load local config "+e.getMessage());
		}
				
	}
	
	public void set_wci_hash(Hashtable<String,String> wci_hash)
	{
		this.wci_hash=wci_hash;
	}
	
	public String one_sw_process(String word,int limit)
	{
		String res="";
		
		//如果单词长度小于6，则该搜索词作为一个单词，存入redis词库里
		if((word.length())<6)
		{
			
		}
		
		String seg_line="";
		seg_line=swa_seg.sim_line_process(word,limit);
		System.out.println("seg_line:"+seg_line);
		String tag_line="";
		tag_line=swa_tag.simple_line_process(seg_line);
		System.out.println("tag_line:"+tag_line);
		res=getWord_det_info(tag_line);
			
		return res;
	}
	
	 public String getWord_det_info(String word)
	 {
	    	String wdi="";
	    	String[] ws=null;
	    	ws=word.split("\\s+");
	    	String one_word="";
	    	Vector wdi_vec=new Vector();
	    	  	
	    	String csw_info="";
	    	
	    	for(int i=0;i<ws.length;i++)
	    	{
	    		one_word=ws[i].trim();
	    		System.out.println("one_word:"+one_word);
	    	    csw_info=wci_hash.get(one_word);
	    	    if((csw_info!=null)&&(!(csw_info.equals(""))))
	    	    {
	    	    	System.out.println("one_word:"+one_word+"  csw_info:"+csw_info);
	    	    	wdi_vec.add(csw_info);  	    	
	    	    }    		   		 		
	    	}
	    	
	       wdi=get_cate_and_small_words(wdi_vec);
	       
	       String[] tt_seg=wdi.split("\001");
	       String cate="";
	       String sw_info="";
	       if((tt_seg.length)==2)
	       {
	         cate=tt_seg[0].trim();
	         sw_info=tt_seg[1].trim();
	         wdi=cate+"\001"+word+" "+sw_info;
	       }
	       else if((tt_seg.length)==1)
	       {
	    	   cate=tt_seg[0].trim();
	    	   wdi=cate+"\001"+word;;
	       }
	       else
	       {
	    	   wdi="NA\001"+word;
	       }
	       wdi=wdi.trim();
	       
	       return wdi;
	    }
	    
	    
	    
	    public String get_cate_and_small_words(Vector vec)
	    {
	    	String temp_csw="";
	    	Hashtable<String,Integer> cate_hash=new Hashtable<String,Integer>();
	    	
	    	String one_csw="";
	    	String cate="";
	    	String sw_info="";
	    	
	    	String[] temp_seg=null;
	    	int old_count=0;
	    	for(int i=0;i<vec.size();i++)
	    	{
	    		one_csw=vec.get(i)+"";
	    		temp_seg=one_csw.split("\001");
	    		if((temp_seg.length)<1)
	    		{
	    			continue;
	    		}    
	    		cate=temp_seg[0].trim();
	    		sw_info="";
	    		if((temp_seg.length)>1)
	    		{
	    		sw_info=temp_seg[1].trim();
	    		}
	    		if(!(cate_hash.containsKey(cate)))
	    		{
	    			cate_hash.put(cate, 1);
	    		}
	    		else
	    		{
	    		    old_count=Integer.parseInt(cate_hash.get(cate)+"");
	    		    cate_hash.remove(cate);
	    		    old_count=old_count+1;    		    
	    		    cate_hash.put(cate, old_count);
	    		}    		   		
	    	}
	    	
	    	
	    	Enumeration cate_enum=cate_hash.keys();
	    	int temp_num=0;
	    	int max_num=0;
	    	String max_cate="NA";
	    	while(cate_enum.hasMoreElements())
	    	{
	    		cate=cate_enum.nextElement()+"";
	    		cate=cate.trim();
	    		if((cate==null)||(cate.equals("")))
	    		{
	    			continue;
	    		}
	    		temp_num=cate_hash.get(cate);
	    		if(temp_num>max_num)
	    		{
	    			max_num=temp_num;
	    			max_cate=cate;
	    		}  		
	    	}
	    	
	    	String all_sw_info="";
	    	for(int i=0;i<vec.size();i++)
	    	{
	    		sw_info="";
	    		one_csw=vec.get(i)+"";
	    		temp_seg=one_csw.split("\001");
	    		if((temp_seg.length)<1)
	    		{
	    			continue;
	    		}    
	    		cate=temp_seg[0].trim();
	    		if((temp_seg.length)>1)
	    		{
	    		sw_info=temp_seg[1].trim();
	    		}
	            if(cate.equals(max_cate))
	            {
	            	all_sw_info=all_sw_info+sw_info+" ";
	            }
	    	}
	    	all_sw_info=all_sw_info.trim();
	    	
	    	temp_csw=cate+"\001"+all_sw_info; 
	       // System.out.println("temp_csw:"+temp_csw);	   	
	    	return temp_csw;
	   }
	   
	    
	    
	
	
}
