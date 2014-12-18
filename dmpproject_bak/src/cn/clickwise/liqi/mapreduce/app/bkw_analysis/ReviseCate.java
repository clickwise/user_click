package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;


public class ReviseCate {

	public void revise_cate(String source_sample,String dest_sample) throws Exception
	{
	  	FileReader sou_fr=new FileReader(new File(source_sample));
	  	BufferedReader sou_br=new BufferedReader(sou_fr);
	  	String line="";
	  	String cate_str="";
	  	String[] seg_arr=null;
	  	String title="";
	  	String old_cate="";
	  	String new_cate="";
	  	
	  	FileWriter des_fw=new FileWriter(new File(dest_sample));
	  	PrintWriter des_pw=new PrintWriter(des_fw);
	  	while((line=sou_br.readLine())!=null)
	  	{
	  		line=line.trim();
	  		if((line==null)||(line.equals("")))
	  		{
	  			continue;
	  		}
	  		seg_arr=line.split("\001");
	  		if(seg_arr.length<2)
	  		{
	  			continue;
	  		}
	  		old_cate=seg_arr[0].trim();
	  		title=seg_arr[1].trim();
	  		if(old_cate.equals("健康及健身@疾病资讯"))
	  		{
	  			new_cate="健康及健身@疾病资讯";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("黄色网站@黄色站点"))
	  		{
	  			new_cate="黄色网站@黄色站点";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("兴趣爱好@摄影"))
	  		{
	  			new_cate="兴趣爱好@摄影";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("社区论坛@聊天灌水"))
	  		{
	  			new_cate="社区论坛@聊天灌水";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("教育@普通话"))
	  		{
	  			new_cate="教育@普通话";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("教育@大学教育"))
	  		{
	  			new_cate="教育@学校教育";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("兴趣爱好@花鸟园艺及宠物"))
	  		{
	  			new_cate="兴趣爱好@花鸟园艺及宠物";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("汽车@汽车修理"))
	  		{
	  			new_cate="汽车@买卖汽车";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("商业@玻璃业"))
	  		{
	  			new_cate="商业@机械建材";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("科学@科技太空"))
	  		{
	  			new_cate="社区论坛@聊天灌水";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("家居@装修"))
	  		{
	  			new_cate="家居@装修";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("招聘@职位搜索"))
	  		{
	  			new_cate="招聘@职位搜索";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("数码@数码产品"))
	  		{
	  			new_cate="数码@数码产品";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("旅游@酒店住宿"))
	  		{
	  			new_cate="旅游@酒店住宿";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("体育@篮球"))
	  		{
	  			new_cate="体育@篮球";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("风格时尚@丰胸减肥"))
	  		{
	  			new_cate="女性时尚@丰胸减肥";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("教育@公务员考试"))
	  		{
	  			new_cate="教育@公务员考试";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("教育@英语或第二语言"))
	  		{
	  			new_cate="教育@英语或第二语言";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("招聘@简历写作"))
	  		{
	  			new_cate="招聘@简历写作";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("经济@税收"))
	  		{
	  			new_cate="经济@税收";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("新闻资讯@军事"))
	  		{
	  			new_cate="新闻资讯@军事";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("经济@彩票"))
	  		{
	  			new_cate="彩票@彩票";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("教育@考研"))
	  		{
	  			new_cate="教育@考研";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("兴趣爱好@烟草"))
	  		{
	  			new_cate="噪音@噪音信息";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("科学@星座"))
	  		{
	  			new_cate="星座算卦@星座";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("饮食@中餐"))
	  		{
	  			new_cate="饮食@中餐";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("健康及健身@医院"))
	  		{
	  			new_cate="健康及健身@医院";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("体育@综合体育"))
	  		{
	  			new_cate="体育@综合体育";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("宗教@基督教"))
	  		{
	  			new_cate="噪音@噪音信息";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("兴趣爱好@电子游戏"))
	  		{
	  			new_cate="游戏@游戏";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("商业@农业"))
	  		{
	  			new_cate="商业@农业";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("数码@编程语言"))
	  		{
	  			new_cate="数码@编程语言";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("饮食@酒水"))
	  		{
	  			new_cate="噪音@噪音信息";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("家庭及教养@母婴"))
	  		{
	  			new_cate="母婴@母婴";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("商业@建筑业"))
	  		{
	  			new_cate="商业@机械建材";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("风格时尚@美妆"))
	  		{
	  			new_cate="女性时尚@美妆";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("科学@算卦"))
	  		{
	  			new_cate="星座算卦@算卦";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("导航@导航"))
	  		{
	  			new_cate="导航@导航";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("商业@物流业"))
	  		{
	  			new_cate="商业@物流业";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("风格时尚@女性话题"))
	  		{
	  			new_cate="女性时尚@女性话题";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("经济@保险"))
	  		{
	  			new_cate="经济@保险";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("噪音@噪音信息"))
	  		{
	  			new_cate="噪音@噪音信息";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("汽车@汽车文化"))
	  		{
	  			new_cate="汽车@买卖汽车";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("旅游@旅游"))
	  		{
	  			new_cate="旅游@旅游";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	  		
	  		else if(old_cate.equals("文化娱乐@音乐"))
	  		{
	  			new_cate="音乐@音乐";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("健康及健身@药品"))
	  		{
	  			new_cate="健康及健身@药品";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("汽车@二手车"))
	  		{
	  			new_cate="汽车@二手车";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("经济@基金股票"))
	  		{
	  			new_cate="经济@基金股票";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("社交@视频秀"))
	  		{
	  			new_cate="社交@视频秀";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("社区论坛@地方门户"))
	  		{
	  			new_cate="社区论坛@地方门户";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("文化娱乐@书籍文学"))
	  		{
	  			new_cate="小说@小说";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("旅游@飞机和火车票"))
	  		{
	  			new_cate="旅游@飞机和火车票";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("新闻资讯@新闻"))
	  		{
	  			new_cate="新闻资讯@新闻";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("健康及健身@锻炼"))
	  		{
	  			new_cate="噪音@噪音信息";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("文化娱乐@动漫"))
	  		{
	  			new_cate="动画@动漫";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("教育@幼儿教育"))
	  		{
	  			continue;
	  		}
	  		else if(old_cate.equals("汽车@摩托车"))
	  		{
	  			new_cate="汽车@摩托车";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("文化娱乐@明星八卦"))
	  		{
	  			new_cate="文娱@明星八卦";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("文化娱乐@幽默搞笑"))
	  		{
	  			new_cate="笑话@幽默搞笑";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("政府法制@政治"))
	  		{
	  			new_cate="噪音@噪音信息";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("科学@化学"))
	  		{
	  			continue;
	  		}
	  		else if(old_cate.equals("数码@电脑维修"))
	  		{
	  			new_cate="数码@数码产品";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("旅游@公交出巡"))
	  		{
	  			new_cate="旅游@公交出巡";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("兴趣爱好@绘画"))
	  		{
	  			new_cate="兴趣爱好@绘画";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	  		
	  		else if(old_cate.equals("商业@机械设备"))
	  		{
	  			new_cate="商业@机械建材";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("商业@服务行业"))
	  		{
	  			continue;
	  		}
	  		else if(old_cate.equals("购物@优惠券"))
	  		{
	  			new_cate="购物@优惠券";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("商业@广告"))
	  		{
	  			continue;
	  		}
	  		else if(old_cate.equals("教育@小学教育"))
	  		{
	  			new_cate="教育@学校教育";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("社区论坛@生活社区"))
	  		{
	  			continue;
	  		}	
	  		else if(old_cate.equals("经济@财经新闻"))
	  		{
	  			continue;
	  		}
	  		else if(old_cate.equals("饮食@特产"))
	  		{
	  			continue;
	  		}
	  		else if(old_cate.equals("家居@二手房"))
	  		{
	  			new_cate="家居@二手房";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("家居@租房"))
	  		{
	  			new_cate="家居@租房";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("教育@中学教育"))
	  		{
	  			new_cate="教育@学校教育";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("数码@硬件设计"))
	  		{
	  			new_cate="数码@数码产品";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("社交@婚姻"))
	  		{
	  			continue;
	  		}
	  		else if(old_cate.equals("兴趣爱好@棋牌"))
	  		{
	  			new_cate="兴趣爱好@棋牌";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("经济@信用贷款"))
	  		{
	  			new_cate="经济@信用贷款";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("兴趣爱好@藏品"))
	  		{
	  			new_cate="兴趣爱好@藏品";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("宗教@佛教"))
	  		{
	  			new_cate="噪音@噪音信息";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("兴趣爱好@工艺品"))
	  		{
	  			continue;
	  		}
	  		else if(old_cate.equals("教育@成人教育"))
	  		{
	  			continue;
	  		}
	  		else if(old_cate.equals("科学@生物学"))
	  		{
	  			continue;
	  			
	  		}	  		
	  		else if(old_cate.equals("兴趣爱好@珠饰"))
	  		{
	  			continue;
	  		}	
	  		else if(old_cate.equals("教育@高考"))
	  		{
	  			new_cate="教育@高考";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("文化娱乐@综艺"))
	  		{
	  			new_cate="视频@综艺";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("科学@天气"))
	  		{
	  			new_cate="天气@天气";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("教育@教师考试"))
	  		{
	  			new_cate="教育@公务员考试";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("经济@银行存款"))
	  		{
	  			continue;
	  		}	
	  		else if(old_cate.equals("社交@约会交友"))
	  		{
	  			new_cate="社交@约会交友";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("购物@电商"))
	  		{
	  			new_cate="购物@电商";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("体育@足球"))
	  		{
	  			new_cate="体育@足球";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("家居@房地产"))
	  		{
	  			new_cate="家居@房地产";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("购物@团购"))
	  		{
	  			new_cate="团购@团购";
	  			des_pw.println(new_cate+"\001"+title);
	  		}	
	  		else if(old_cate.equals("文化娱乐@电视剧"))
	  		{
	  			new_cate="视频@电视剧";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("汽车@买卖汽车"))
	  		{
	  			new_cate="汽车@买卖汽车";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("文化娱乐@电影"))
	  		{
	  			new_cate="视频@电影";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("教育@学校管理"))
	  		{
	  			continue;
	  		}
	  		else if(old_cate.equals("饮食@茶品"))
	  		{
	  			continue;
	  		}	
	  		else if(old_cate.equals("教育@特殊教育"))
	  		{
	  			new_cate="教育@特殊教育";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("政府法制@法律"))
	  		{
	  			new_cate="法律@法律";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("科学@物理"))
	  		{
	  			continue;
	  		}
	  		else if(old_cate.equals("数码@软件"))
	  		{
	  			new_cate="数码@软件";
	  			des_pw.println(new_cate+"\001"+title);
	  		}
	  		else if(old_cate.equals("科学@数学"))
	  		{
	  			continue;
	  		}
	  		
	  		
	  		
	  	}
	  	
	  	sou_fr.close();
	  	sou_br.close();
	  	des_fw.close();
	  	des_pw.close();
	  	
	  	
	  	
	  	
	}
	
	public void gen_labels(String sample_file) throws Exception
	{
		FileReader fr=new FileReader(new File(sample_file));
		BufferedReader br=new BufferedReader(fr);
		String line="";
		String cate_str="";
		String first_cate="";
		String second_cate="";
		String[] seg_arr=null;
		String title="";
		String[] cate_seg=null;
		Hashtable<String,Integer> first_hash=new Hashtable<String,Integer>();
		Hashtable<String,Integer> second_hash=new Hashtable<String,Integer>();
		int first_index=0;
		int second_index=0;
		
		FileWriter first_fw=new FileWriter(new File("temp/first_nart_names.txt"));
		PrintWriter first_pw=new PrintWriter(first_fw);
		
		FileWriter second_fw=new FileWriter(new File("temp/second_nart_names.txt"));
		PrintWriter second_pw=new PrintWriter(second_fw);
		
		FileWriter label_str_fw=new FileWriter(new File("temp/label_nart_str.txt"));
		PrintWriter label_str_pw=new PrintWriter(label_str_fw);
		int first_old_index=0;
		int second_old_index=0;
		String fs_str="";
		Hashtable str_hash=new Hashtable();
		
		FileWriter label_name_fw=new FileWriter(new File("temp/label_nart_name.txt"));
		PrintWriter label_name_pw=new PrintWriter(label_name_fw);
		Hashtable name_hash=new Hashtable();
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
	  		{
	  			continue;
	  		}
			
			seg_arr=line.split("\001");
			if(seg_arr.length<2)
			{
				continue;
			}
			cate_str=seg_arr[0].trim();
			title=seg_arr[1].trim();
			if((cate_str==null)||(cate_str.equals("")))
	  		{
	  			continue;
	  		}
			
			if(!name_hash.containsKey(cate_str))
			{
				name_hash.put(cate_str, 1);
				label_name_pw.println(cate_str);
			}
			cate_seg=cate_str.split("@");
			if(cate_seg.length<2)
			{
				continue;
			}
			first_cate=cate_seg[0].trim();
			second_cate=cate_seg[1].trim();
			if(!first_hash.containsKey(first_cate))
			{
				first_index++;
				first_hash.put(first_cate, first_index);
				first_pw.println(first_cate+" "+first_index);
				first_old_index=first_index;
			}
			else
			{
				first_old_index=first_hash.get(first_cate);
			}
			if(!second_hash.containsKey(second_cate))
			{
				second_index++;
				second_hash.put(second_cate, second_index);	
				second_pw.println(second_cate+" "+second_index);
				second_old_index=second_index;
			}
			else
			{
				second_old_index=second_hash.get(second_cate);
			}
			
			fs_str=first_old_index+"_"+second_old_index;
		    if(!str_hash.containsKey(fs_str))
		    {
		    	str_hash.put(fs_str, 1);
		    	label_str_pw.println(fs_str);
		    }
			
			
		}
		fr.close();
		br.close();
		first_fw.close();
		first_pw.close();
		second_fw.close();
		second_pw.close();
		label_str_pw.close();
		label_str_fw.close();
		label_name_fw.close();
		label_name_pw.close();
	}
	
	public void selectTopSample(String sample_file,String output_file,int N) throws Exception
	{
		Hashtable<String,Integer> label_hash=new Hashtable<String,Integer>();
		String line="";
		FileReader fr=new FileReader(new File(sample_file));
		BufferedReader br=new BufferedReader(fr);
		
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);
		
		String first_cate="";
		String second_cate="";
		String[] seg_arr=null;
		String label_str="";
		int old_count=0;
		int nc=0;
		while((line=br.readLine())!=null)
		{
			if((line==null)||(line.equals("")))
	  		{
	  			continue;
	  		}
			seg_arr=line.split("\\s+");
			if(seg_arr.length<10)
			{
				continue;
			}
			first_cate=seg_arr[0].trim();
			second_cate=seg_arr[1].trim();
			label_str=first_cate+"_"+second_cate;
			if(!label_hash.containsKey(label_str))
			{
				pw.println(line);
				nc++;
				label_hash.put(label_str, 1);
			}
			else
			{
				old_count=label_hash.get(label_str);
				old_count++;
				label_hash.remove(label_str);
				label_hash.put(label_str, old_count);
				if(old_count<N)
				{
					pw.println(line);
					nc++;
				}
			}
						
		}
		System.out.println("nc:"+nc);
		pw.close();
		fw.close();
		fr.close();
		br.close();
		
		
	}
	
	public static void main(String[] args) throws Exception
	{
		String source_file="input/user_sample_ot_revise.txt";
		String dest_file="output/user_sample_ot_nart.txt";
		ReviseCate rc=new ReviseCate();
		String format_file="input/title_nntrain.txt";
		String top_format_file="output/top1000_format_nart_file.txt";
		//rc.revise_cate(source_file, dest_file);
		//rc.gen_labels(dest_file);
		int N=1000;
		rc.selectTopSample(format_file, top_format_file, N);				
	}
	
	
}
