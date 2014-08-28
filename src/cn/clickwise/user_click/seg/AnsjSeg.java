package cn.clickwise.user_click.seg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import cn.clickwise.liqi.file.uitls.FileReaderUtil;
import cn.clickwise.liqi.str.basic.DS2STR;
import cn.clickwise.liqi.str.edcode.Base64Code;
import cn.clickwise.liqi.str.basic.SSO;
/**
 * 分词的封装，模型较小可放在jar包中，
 * 加入切分词典、停用词典
 * @author zkyz
 */
public class AnsjSeg {

	private HashMap<String,String> seg_dict;
	private HashMap<String,String> stop_dict;
	
	public String seg(String text)
	{
		String ansi_seg=segAnsi(text);
		String mseg=merge_sen_limit(ansi_seg,6);
		//String rseg=removeStopWords(mseg);
		return mseg;
	}
	
	public String segAnsi(String text) {
		String segs = "";
		List<Term> parse2 = ToAnalysis.parse(text);
		StringBuilder sb = new StringBuilder();
		for (Term term : parse2) {
			sb.append(term.getName());
			sb.append(" ");
		}
		segs = sb.toString();
		segs = segs.trim();
		return segs;
	}

	public String merge_sen_limit(String stanf_seg_text, int limit) {
		String m_s = "";

        if(SSO.tioe(stanf_seg_text))
        {
              return "";
        }
        stanf_seg_text=stanf_seg_text.trim();

		String[] stanf_seg_arr = stanf_seg_text.split("\\s+");
        if(stanf_seg_arr.length<2)
        {
             return stanf_seg_text; 
        }
		String[] one_step_words = new String[stanf_seg_arr.length];
		int one_step_i = 0;

		for (int i = 0; i < one_step_words.length; i++) {
			one_step_words[i] = "";
		}
		String[] temp_stanf_arr = new String[stanf_seg_arr.length];
		int temp_arr_i = 0;
		String temp_arr_key = "";

		for (int i = 0; i < stanf_seg_arr.length; i++) {
			temp_arr_key = stanf_seg_arr[i].trim();
			if (temp_arr_key.length() < 1) {
				continue;
			} else {
				temp_stanf_arr[temp_arr_i++] = temp_arr_key;
			}
		}

		stanf_seg_arr = new String[temp_stanf_arr.length];
		for (int i = 0; i < temp_stanf_arr.length; i++) {
			stanf_seg_arr[i] = temp_stanf_arr[i];
		}

		String temp_words = "";
		String temp_three_words = "";
		String temp_four_words = "";
		String temp_five_words = "";
		String temp_six_words = "";
		for (int i = 0; i < (stanf_seg_arr.length); i++) {
			temp_words = "";
			if (i < (stanf_seg_arr.length - 1)) {
				temp_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1];
				temp_words = temp_words.trim();
			}
			temp_three_words = "";
			if (i < (stanf_seg_arr.length - 2)) {
				temp_three_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2];
				temp_three_words = temp_three_words.trim();
				 ////System.out.println("temp_three_words:"+temp_three_words);
			}

			temp_four_words = "";
			if (i < (stanf_seg_arr.length - 3)) {
				temp_four_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2] + stanf_seg_arr[i + 3];
				temp_four_words = temp_four_words.trim();
				 ////System.out.println("temp_four_words:"+temp_three_words);
			}

			temp_five_words = "";
			if (i < (stanf_seg_arr.length - 4)) {
				temp_five_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2] + stanf_seg_arr[i + 3]
						+ stanf_seg_arr[i + 4];
				temp_five_words = temp_five_words.trim();
				 //System.out.println("temp_five_words:"+temp_three_words);
			}

			temp_six_words = "";
			if (i < (stanf_seg_arr.length - 5)) {
				temp_six_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2] + stanf_seg_arr[i + 3]
						+ stanf_seg_arr[i + 4] + stanf_seg_arr[i + 5];
				temp_six_words = temp_six_words.trim();
				 //System.out.println("temp_six_words:"+temp_three_words);
			}

			if ((temp_words.length() > 0)
					&& (temp_words.length() < limit)
					&& (seg_dict.containsKey(temp_words))) {
				one_step_words[one_step_i++] = temp_words;
				 //System.out.println("two_temp_words:"+temp_words);
				i++;
			} else if ((temp_three_words.length() > 0)
					&& (temp_three_words.length() < limit)
					&& (seg_dict.containsKey(temp_three_words))) {
				one_step_words[one_step_i++] = temp_three_words;
				 //System.out.println("three_temp_words:"+temp_words);
				i = i + 2;
			} else if ((temp_four_words.length() > 0)
					&& (temp_four_words.length() < limit)
					&& (seg_dict.containsKey(temp_four_words))) {
				one_step_words[one_step_i++] = temp_four_words;
				 //System.out.println("four_temp_words:"+temp_words);
				i = i + 3;
			} else if ((temp_five_words.length() > 0)
					&& (temp_five_words.length() < limit)
					&& (seg_dict.containsKey(temp_five_words))) {
				one_step_words[one_step_i++] = temp_five_words;
				 //System.out.println("five_temp_words:"+temp_words);
				i = i + 4;
			} else if ((temp_six_words.length() > 0)
					&& (temp_six_words.length() < limit)
					&& (seg_dict.containsKey(temp_six_words))) {
				one_step_words[one_step_i++] = temp_six_words;
				 //System.out.println("six_temp_words:"+temp_words);
				i = i + 5;
			} else {
				if (i < (stanf_seg_arr.length - 2)) {
					one_step_words[one_step_i++] = stanf_seg_arr[i];
					 //System.out.println("in else stanf seg :"+(one_step_i-1)+"  "+stanf_seg_arr[i]);
				} else if (i == (stanf_seg_arr.length - 2)) {
					one_step_words[one_step_i++] = stanf_seg_arr[i];
				         //System.out.println("in else if stanf seg :"+(one_step_i-1)+"  "+stanf_seg_arr[i]);
					one_step_words[one_step_i++] = stanf_seg_arr[i + 1];
					 //System.out.println("stanf seg :"+(one_step_i-1)+"  "+stanf_seg_arr[i+1]);
					break;
				} else if (i == (stanf_seg_arr.length - 1)) {
					one_step_words[one_step_i++] = stanf_seg_arr[i];
					 //System.out.println("stanf seg :"+(one_step_i-1)+"  "+stanf_seg_arr[i]);
					break;
				}
			}
		}

		String[] two_step_words = new String[one_step_words.length];
		int two_step_i = 0;
		for (int i = 0; i < two_step_words.length; i++) {
			two_step_words[i] = "";
		}

		// //System.out.println("one_step_i:"+one_step_i);
		for (int i = 0; i < (one_step_i); i++) {
			temp_words = "";
			temp_words = one_step_words[i] + one_step_words[i + 1];
			temp_words = temp_words.trim();

			temp_three_words = "";
			if (i < (one_step_i - 2)) {
				temp_three_words = one_step_words[i] + one_step_words[i + 1]
						+ one_step_words[i + 2];
				temp_three_words = temp_three_words.trim();
				 //System.out.println("temp_three_words:"+temp_three_words);
			}

			temp_four_words = "";
			if (i < (one_step_i - 3)) {
				temp_four_words = one_step_words[i] + one_step_words[i + 1]
						+ one_step_words[i + 2] + one_step_words[i + 3];
				temp_four_words = temp_four_words.trim();
				 //System.out.println("temp_four_words:"+temp_four_words);
			}

			temp_five_words = "";
			if (i < (one_step_i - 4)) {
				temp_five_words = one_step_words[i] + one_step_words[i + 1]
						+ one_step_words[i + 2] + one_step_words[i + 3]
						+ one_step_words[i + 4];
				temp_five_words = temp_five_words.trim();
				 //System.out.println("temp_five_words:"+temp_five_words);
			}

			temp_six_words = "";
			if (i < (one_step_i - 5)) {
				temp_six_words = one_step_words[i] + one_step_words[i + 1]
						+ one_step_words[i + 2] + one_step_words[i + 3]
						+ one_step_words[i + 4] + one_step_words[i + 5];
				temp_six_words = temp_six_words.trim();
			        //System.out.println("temp_six_words:"+temp_six_words);
			}

			if ((temp_words.length() > 0)
					&& (temp_words.length() < limit)
					&& (seg_dict.containsKey(temp_words))) {
				two_step_words[two_step_i++] = temp_words;
				i++;
			}

			else if ((temp_three_words.length() > 0)
					&& (temp_three_words.length() < limit)
					&& (seg_dict.containsKey(temp_three_words))) {
				two_step_words[two_step_i++] = temp_three_words;
			        //System.out.println("temp_three_words:"+temp_three_words);
				i = i + 2;
			} else if ((temp_four_words.length() > 0)
					&& (temp_four_words.length() < limit)
					&& (seg_dict.containsKey(temp_four_words))) {
				two_step_words[two_step_i++] = temp_four_words;
				 //System.out.println("temp_four_words:"+temp_four_words);
				i = i + 3;
			} else if ((temp_five_words.length() > 0)
					&& (temp_five_words.length() < limit)
					&& (seg_dict.containsKey(temp_five_words))) {
				two_step_words[two_step_i++] = temp_five_words;
				 //System.out.println("temp_five_words:"+temp_five_words);
				i = i + 4;
			} else if ((temp_six_words.length() > 0)
					&& (temp_six_words.length() < limit)
					&& (seg_dict.containsKey(temp_six_words))) {
				two_step_words[two_step_i++] = temp_six_words;
				//System.out.println("temp_six_words:"+temp_six_words);
				i = i + 5;
			}

			else {
				if (i < (one_step_i - 2)) {
					two_step_words[two_step_i++] = one_step_words[i];
				} else if (i == (one_step_i - 2)) {
					two_step_words[two_step_i++] = one_step_words[i];
					two_step_words[two_step_i++] = one_step_words[i + 1];
					break;
				} else if (i == (one_step_i - 1)) {
					two_step_words[two_step_i++] = one_step_words[i];
					break;
				}
			}
		}

		String[] three_step_words = new String[two_step_words.length];
		for (int i = 0; i < three_step_words.length; i++) {
			three_step_words[i] = "";
		}
		int three_step_i = 0;
		for (int i = 0; i < (two_step_i); i++) {
			temp_words = "";
			temp_words = two_step_words[i] + two_step_words[i + 1];
			if ((temp_words.length() > 0)
					&& (temp_words.length() < limit)
					&& (seg_dict.containsKey(temp_words))) {
				three_step_words[three_step_i++] = temp_words;
				i++;
			}
			else {
				if (i < (two_step_words.length - 2)) {
					three_step_words[three_step_i++] = two_step_words[i];
				} else if (i == (two_step_words.length - 2)) {
					three_step_words[three_step_i++] = two_step_words[i];
					three_step_words[three_step_i++] = two_step_words[i + 1];
					break;
				} else if (i == (two_step_words.length - 1)) {
					three_step_words[three_step_i++] = two_step_words[i];
					break;
				}

			}

		}

		String[] four_step_words = new String[three_step_words.length];
		for (int i = 0; i < four_step_words.length; i++) {
			four_step_words[i] = "";
		}
		int four_step_i = 0;
		for (int i = 0; i < (three_step_i); i++) {
			temp_words = "";
			temp_words = three_step_words[i] + three_step_words[i + 1];

			if ((temp_words.length() > 0)
					&& (temp_words.length() < limit)
					&& (seg_dict.containsKey(temp_words))) {
				four_step_words[four_step_i++] = temp_words;
				i++;
			}
			else {
				if (i < (three_step_words.length - 2)) {
					four_step_words[four_step_i++] = three_step_words[i];
				} else if (i == (three_step_words.length - 2)) {
					four_step_words[four_step_i++] = three_step_words[i];
					four_step_words[four_step_i++] = three_step_words[i + 1];
					break;
				} else if (i == (three_step_words.length - 1)) {
					four_step_words[four_step_i++] = three_step_words[i];
					break;
				}
			}

		}

		String[] five_step_words = new String[four_step_words.length];
		for (int i = 0; i < five_step_words.length; i++) {
			five_step_words[i] = "";
		}
		int five_step_i = 0;
		for (int i = 0; i < (four_step_i); i++) {
			temp_words = "";
			temp_words = four_step_words[i] + four_step_words[i + 1];
			if ((temp_words.length() > 0)
					&& (temp_words.length() < limit)
					&& (seg_dict.containsKey(temp_words))) {
				five_step_words[five_step_i++] = temp_words;
				i++;
			} else {
				if (i < (four_step_words.length - 2)) {
					five_step_words[five_step_i++] = four_step_words[i];
				} else if (i == (four_step_words.length - 2)) {
					five_step_words[five_step_i++] = four_step_words[i];
					five_step_words[five_step_i++] = four_step_words[i + 1];
					break;
				} else if (i == (four_step_words.length - 1)) {
					five_step_words[five_step_i++] = four_step_words[i];
					break;
				}
			}

		}

		String[] six_step_words = new String[five_step_words.length];
		for (int i = 0; i < six_step_words.length; i++) {
			six_step_words[i] = "";
		}
		int six_step_i = 0;
		for (int i = 0; i < (five_step_i); i++) {
			temp_words = "";
			temp_words = five_step_words[i] + five_step_words[i + 1];
			if ((temp_words.length() > 0)
					&& (temp_words.length() < limit)
					&& (seg_dict.containsKey(temp_words))) {
				six_step_words[six_step_i++] = temp_words;
				i++;
			} else {
				if (i < (five_step_words.length - 2)) {
					six_step_words[six_step_i++] = five_step_words[i];
				} else if (i == (five_step_words.length - 2)) {
					six_step_words[six_step_i++] = five_step_words[i];
					six_step_words[six_step_i++] = five_step_words[i + 1];
					break;
				} else if (i == (five_step_words.length - 1)) {
					six_step_words[six_step_i++] = five_step_words[i];
					break;
				}
			}
		}

		String[] seven_step_words = new String[six_step_words.length];
		for (int i = 0; i < seven_step_words.length; i++) {
			seven_step_words[i] = "";
		}
		int seven_step_i = 0;
		for (int i = 0; i < (six_step_i); i++) {
			temp_words = "";
			temp_words = six_step_words[i] + six_step_words[i + 1];
			if ((temp_words.length() > 0)
					&& (temp_words.length() < limit)
					&& (seg_dict.containsKey(temp_words))) {
				seven_step_words[seven_step_i++] = temp_words;
				i++;
			} else {
				if (i < (six_step_words.length - 2)) {
					seven_step_words[seven_step_i++] = six_step_words[i];
				} else if (i == (six_step_words.length - 2)) {
					seven_step_words[seven_step_i++] = six_step_words[i];
					seven_step_words[seven_step_i++] = six_step_words[i + 1];
					break;
				} else if (i == (six_step_words.length - 1)) {
					seven_step_words[seven_step_i++] = six_step_words[i];
					seven_step_words[seven_step_i++] = six_step_words[i + 1];
					break;
				}
			}
		}

		String nword = "";
		for (int i = 0; i < seven_step_words.length; i++) {
			temp_words = seven_step_words[i];
			if (temp_words == null) {
				continue;
			}
			temp_words = temp_words.trim();
			nword = clean_one_word(temp_words);
			nword = nword.trim();
			if ((nword == null) || nword.equals("null")) {
				continue;
			}
			if (!(nword.equals(""))) {
				m_s = m_s + nword + " ";
			}
			// //System.out.println(i + "  " + nword);
		}

		return m_s;
	}
	
	public String clean_one_word(String word) {
		String new_word = word;
		// new_word=new_word.replaceAll("``", "");
		// new_word=new_word.replaceAll("''", "");
		new_word = new_word.replaceAll("&nbsp;", "");
		new_word = new_word.replaceAll("&nbsp", "");
		new_word = new_word.replaceAll("&ldquo;", "");
		new_word = new_word.replaceAll("&ldquo", "");
		new_word = new_word.replaceAll("&rdquo;", "");
		new_word = new_word.replaceAll("&rdquo", "");
		// new_word=new_word.replaceAll(";", "");
		new_word = new_word.replaceAll("&", "");
		new_word = new_word.replaceAll("VS", "");
		new_word = new_word.replaceAll("-RRB-", "");
		new_word = new_word.replaceAll("-LRB-", "");
		new_word = new_word.replaceAll("_", "");
		// new_word=new_word.replaceAll("[\\.]*", "");
		new_word = new_word.replaceAll("\\\\\\#", "");
		/*
		 * if (Pattern.matches("[a-zA-Z\\,\\.\\?0-9\\!\\-\\s]*", new_word)) {
		 * return ""; } if (!(Pattern.matches("[\\u4e00-\\u9fa5]+", new_word)))
		 * { return ""; }
		 */
		new_word = new_word.replaceAll("★", "");
		new_word = new_word.replaceAll("__", "");
		new_word = new_word.replaceAll("ˇ", "");
		new_word = new_word.replaceAll("®", "");
		new_word = new_word.replaceAll("♣", "");
		new_word = new_word.replaceAll("\\丨", "");
		new_word = new_word.trim();

		return new_word;
	}
	
	public String removeStopWords(String seg_text)
	{
		String rev_seg_text="";
		seg_text=seg_text.trim();
		
		String[] words=seg_text.split("\\s+");
		ArrayList<String> wl=new ArrayList<String>();
		
		String word="";
		for(int i=0;i<words.length;i++)
		{
			word=words[i];
			if(stop_dict.containsKey(word))
			{
				continue;
			}
			wl.add(word);
		}
		
		rev_seg_text=DS2STR.arraylist2str(wl);
		
		return rev_seg_text;
	}
	
	public HashMap<String,String> getSeg_dict() {
		return seg_dict;
	}

	public void setSeg_dict(HashMap<String,String> seg_dict) {
		this.seg_dict = seg_dict;
	}

	public HashMap<String,String> getStop_dict() {
		return stop_dict;
	}

	public void setStop_dict(HashMap<String,String> stop_dict) {
		this.stop_dict = stop_dict;
	}
	
	public static void main(String[] args)
	{
		String seg_dict_file="temp/seg_test/five_dict_uniq.txt";
		String stop_dict_file="temp/seg_test/cn_stop_words_utf8.txt";
		AnsjSeg ansjseg=new AnsjSeg();
		HashMap<String,String> seg_dict=FileReaderUtil.file2Hash(seg_dict_file);
		HashMap<String,String> stop_dict=FileReaderUtil.file2Hash(stop_dict_file);
		ansjseg.setSeg_dict(seg_dict);
		ansjseg.setStop_dict(stop_dict);
		
		String text="凤凰网 凤凰网是中国领先的综合门户网站，提供含文图音视频的全方位综合新闻资讯、深度访谈、观点评论、财经产品、互动应用、分享社区等服务，同时与凤凰无线、凤凰宽频形成动，为全球主流华人提供互联网、无线通信、电视网三网融合无缝衔接的新媒体优质体验。";
		//System.out.println("text:"+text);
		System.out.println("segt:"+ansjseg.seg(text));
	
		
		text="北京时间8月27日晚，2014赛季亚冠联赛1/4决赛第二回合展开争夺，广州恒大[微博]主场2-1战胜西悉尼流浪者，但因客场进球少被淘汰。著名足球评论员黄健翔[微博]在新浪体育对本场比赛进行了解说，黄健翔认为对方布里奇制造的点球不该判，恒大运气不好裁判手太松。";
		System.out.println("text:"+text);
		System.out.println("segt:"+ansjseg.seg(text));
	
	}
	
}
