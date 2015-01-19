package cn.clickwise.clickad.classify_pattern;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import cn.clickwise.clickad.keyword.KeyExtract;
import cn.clickwise.clickad.seg.Segmenter;
import cn.clickwise.clickad.tag.PosTagger;
import cn.clickwise.liqi.str.basic.SSO;

public class ClassifierMulticlass extends Classifier{
	
	public ClassifierMulticlass()
	{
		
		super();
		System.out.println("initialize multiclass model");
		try {
			String model_path = "model_0119";
			model=read_model(model_path);
			seg = new Segmenter();
			posTagger = new PosTagger("chinese-nodistsim.tagger");
			ke = new KeyExtract();
			video_dict = getDictFromStream("dict_0119.txt");
			label_names = getIndexLabelFromStream("labeldict_0119.txt");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public ModelParams read_model(String model_path) throws Exception {
		
		System.out.println("read multiclass model");
		ModelParams mp=ClassifierFactory.getModelParams();
		
		InputStream model_is = this.getClass().getResourceAsStream(
				"/" + model_path);
		
		InputStreamReader model_isr = new InputStreamReader(model_is);
		// File model_file = new File(model_path);
		// FileReader fr = new FileReader(model_file);
		BufferedReader br = new BufferedReader(model_isr);

		mp.Version = cut_comment(br.readLine());
		if (verbosity >= 3) {
			logger.info("Version:" + mp.Version);
		}

		mp.NUM_CLASS = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("NUM_CLASS:" + mp.NUM_CLASS);
		}

		mp.NUM_WORDS = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("NUM_WORDS:" + mp.NUM_WORDS);
		}

		mp.loss_function = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("loss_function:" + mp.loss_function);
		}

		mp.kernel_type = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("kernel_type:" + mp.kernel_type);
		}

		mp.para_d = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("para_d:" + mp.para_d);
		}

		mp.para_g = Double.parseDouble(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("para_g:" + mp.para_g);
		}

		mp.para_s = Double.parseDouble(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("para_s:" + mp.para_s);
		}

		mp.para_r = Double.parseDouble(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("para_r:" + mp.para_r);
		}

		mp.para_u = cut_comment(br.readLine());
		if (verbosity >= 3) {
			logger.info("para_u:" + mp.para_u);
		}

		mp.NUM_FEATURES = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("NUM_FEATURES:" + mp.NUM_FEATURES);
		}

		mp.train_num = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("train_num:" + mp.train_num);
		}

		mp.suv_num = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("suv_num:" + mp.suv_num);
		}

		mp.b = Double.parseDouble(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("b:" + mp.b);
		}

		mp.line_weights = new double[mp.NUM_FEATURES + 2];
		for (int i = 0; i < mp.line_weights.length; i++) {
			mp.line_weights[i] = 0;
		}

		String line = br.readLine();
		StringTokenizer st = new StringTokenizer(line, " ");
		// System.out.println("st.count:" + st.countTokens());
		// System.out.println("end:" + line.substring(0, 1000));

		int current_pos = 0;
		int forward_num = 0;
		String temp_token = "";
		int temp_index;
		double temp_weight;
		int max_index = -1;
		int search_blank = 0;
		// System.out.println("line.length:" + line.length());
		while (current_pos < (line.length())) {

			forward_num = 0;
			temp_token = "";
			while ((current_pos + forward_num) < (line.length())) {	
				if (((line.charAt(current_pos + forward_num)) != ' ')
						&& ((line.charAt(current_pos + forward_num)) != '#')) {
					temp_token = temp_token
							+ line.charAt(current_pos + forward_num);
					forward_num++;
				} else {
					temp_token = temp_token.trim();
					// if(current_pos>26080000)
					// System.out.println("temp_token:"+temp_token);
					if (((temp_token.indexOf(":")) == -1)
							&& (!temp_token.equals(""))) {
						mp.alpha = Double.parseDouble(temp_token);
					} else if ((temp_token.indexOf("qid")) != -1) {
						mp.qid = Integer.parseInt(temp_token.substring(temp_token
								.indexOf(":") + 1), temp_token.length());
					} else if (Pattern.matches("\\d+:[\\d\\.]+", temp_token)) {
						temp_index = Integer.parseInt(temp_token.substring(0,
								temp_token.indexOf(":")));
						temp_weight = Double.parseDouble(temp_token.substring(
								temp_token.indexOf(":") + 1,
								temp_token.length()));
						mp.line_weights[temp_index] = temp_weight;
						if (temp_index > max_index) {
							max_index = temp_index;
						}
					}
					search_blank = 0;
					while ((current_pos + forward_num + search_blank) < line
							.length()) {
						if (line.charAt(current_pos + forward_num
								+ search_blank) == ' ') {
							search_blank++;
						} else {
							break;
						}
					}

					if ((line.charAt(current_pos + forward_num)) == '#') {
						forward_num++;
					}
					current_pos = current_pos + forward_num + search_blank;
					break;
				}
			}
		}
		// fr.close();
		model_is.close();
		model_isr.close();
		br.close();
		return mp;
	}
	
	public Label classify_struct_example(Word[] sample) {

		Label y = null;
		double score = 0;

		Label best_label = null;
		double best_score = -1;

		Word[] fvec = null;
		for (int i = 0; i < model.NUM_CLASS; i++) {
			y = new Label();
			y.first_class = (i + 1);
			fvec = psi(sample, y);
			score = classify_example(fvec);
			System.out.println("y.class:"+y.first_class+" score:"+score);
			if (score > best_score) {
				best_score = score;
				best_label = y;
			}
		}
		
		best_label.score = best_score;
		return best_label;
	}
	
	public Word[] psi(Word[] sample, Label y) {
		Word[] fvec = null;
		int veclength = (sample.length) * model.NUM_CLASS;
		fvec = new Word[veclength];
		for (int i = 0; i < veclength; i++) {
			fvec[i] = new Word();
		}

		int c1 = y.first_class;
		Word temp_word = null;
		int fi = 0;
		// System.out.println();
		for (int i = 0; i < sample.length; i++) {
			temp_word = sample[i];
			// System.out.print(temp_word.wnum+":"+temp_word.weight+" ");
		}
		// System.out.println();
		// System.out.println("y.first_class:"+y.first_class+" "+y.second_class);
		// 第一级类别特征
		for (int i = 0; i < sample.length; i++) {
			temp_word = sample[i];
			fvec[fi].wnum = temp_word.wnum + (c1 - 1) * model.NUM_WORDS;
			fvec[fi].weight = temp_word.weight;
			// System.out.print(fvec[fi].wnum+":"+fvec[fi].weight+" ");
			fi++;

		}

		// System.out.println();
		return fvec;
	}
	
	public HashMap getIndexLabelFromStream(String input_file) {
		// TODO Auto-generated method stub

		HashMap hm = new HashMap();
		String item = "";
		String label = "";
		String index_str = "";
		int index = 0;
		// FileReader fr=null;
		// BufferedReader br=null;
		InputStream model_is = this.getClass().getResourceAsStream(
				"/" + input_file);
		InputStreamReader model_isr = new InputStreamReader(model_is);

		BufferedReader br = new BufferedReader(model_isr);

		String[] seg_arr = null;

		try {
			// fr=new FileReader(new File(input_file));
			// br=new BufferedReader(fr);
			while ((item = br.readLine()) != null) {
				if (!(SSO.tnoe(item))) {
					continue;
				}
				seg_arr = item.split("\\s+");
				if (seg_arr.length != 2) {
					continue;
				}
				label = seg_arr[0].trim();
				index_str = seg_arr[1].trim();

				if (!(SSO.tnoe(index_str))) {
					continue;
				}

				if (!(SSO.tnoe(label))) {
					continue;
				}
				index = Integer.parseInt(index_str);

				if (index < 1) {
					continue;
				}
				hm.put(index_str, label);
			}

			br.close();
			model_is.close();
			model_isr.close();
		} catch (Exception e) {

		}
		return hm;
	}
	
	public static void main(String[] args) throws Exception {

		ClassifierMulticlass cf = new ClassifierMulticlass();
		 String text="凤凰网 凤凰网是中国领先的综合门户网站，提供含文图音视频的全方位综合新闻资讯、深度访谈、观点评论、财经产品、互动应用、分享社区等服务，同时与凤凰无线、凤凰宽频形成动，为全球主流华人提供互联网、无线通信、电视网三网融合无缝衔接的新媒体优质体验。";
		 System.out.println("cate:"+cf.cate(text));

		/*
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String line = "";
		while ((line = br.readLine()) != null) {
			pw.println(cf.cate(line));
		}

		isr.close();
		osw.close();
		br.close();
		pw.close();
		*/

	}

}
