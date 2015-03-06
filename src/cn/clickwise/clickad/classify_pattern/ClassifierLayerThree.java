package cn.clickwise.clickad.classify_pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import cn.clickwise.clickad.keyword.KeyExtract;
import cn.clickwise.clickad.seg.Segmenter;
import cn.clickwise.clickad.tag.PosTagger;
import cn.clickwise.lib.string.SSO;

/**
 * label 为三层次的分类，例如淘宝商品分类
 * @author zkyz
 *
 */
public class ClassifierLayerThree extends Classifier{
	
	public Label[] posslabels=null;
	
	public ClassifierLayerThree()
	{
		super();
		System.out.println("initialize layer three model");
		try {
			String model_path = "eclo_model";
			model=read_model(model_path);
			seg = new Segmenter();
			posTagger = new PosTagger("chinese-nodistsim.tagger");
			ke = new KeyExtract();
			video_dict = getDictFromStream("eclo_dict.txt");
			label_names = getIndexLabelFromStream("eclo_labeldict.txt");
			readPossLabels("eclo_labeldict.txt");
			String text="睡袋 户外加宽加厚保暖 秋冬季超轻成人睡袋野营可拼接双人睡袋 ";
			System.out.println("cate:"+cate(text));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ClassifierLayerThree(String dict)
	{
		super();
		System.out.println("initialize layer three model");
		try {
			String model_path = "eclo_model";
			model=read_model(model_path);
			seg = new Segmenter();
			System.err.println("loading dict "+dict);
			seg.loadAnsjDic(new File(dict));
			posTagger = new PosTagger("chinese-nodistsim.tagger");
			ke = new KeyExtract();
			video_dict = getDictFromStream("eclo_dict.txt");
			label_names = getIndexLabelFromStream("eclo_labeldict.txt");
			readPossLabels("eclo_labeldict.txt");
			String text="睡袋 户外加宽加厚保暖 秋冬季超轻成人睡袋野营可拼接双人睡袋 ";
			System.out.println("cate:"+cate(text));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ModelParams read_model(String model_path) throws Exception {
		
		System.out.println("read layer three model");
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

		/*
		mp.NUM_CLASS = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("NUM_CLASS:" + mp.NUM_CLASS);
		}
        */
		
		mp.NUM_FIRST_CLASS = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("NUM_FIRST_CLASS:" + mp.NUM_FIRST_CLASS);
			System.err.println("NUM_FIRST_CLASS:" + mp.NUM_FIRST_CLASS);
		}
		
		mp.NUM_SECOND_CLASS = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("NUM_SECOND_CLASS:" + mp.NUM_SECOND_CLASS);
			System.err.println("NUM_SECOND_CLASS:" + mp.NUM_SECOND_CLASS);
		}
		
		mp.NUM_THIRD_CLASS = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("NUM_THIRD_CLASS:" + mp.NUM_THIRD_CLASS);
			System.err.println("NUM_THIRD_CLASS:" + mp.NUM_THIRD_CLASS);
		}
		
		mp.NUM_WORDS = Integer.parseInt(cut_comment(br.readLine()));
		if (verbosity >= 3) {
			logger.info("NUM_WORDS:" + mp.NUM_WORDS);
			System.err.println("NUM_WORDS:" + mp.NUM_WORDS);
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
			System.err.println("NUM_FEATURES:" + mp.NUM_FEATURES);
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

	@Override
	public Label classify_struct_example(Word[] sample) {

		Label y = new Label();
		int  bestfirst = -1;
		int bestsecond = -1;
		int  bestthird = -1;
		int  j;
		boolean first = true;
		double score=0.0, bestscore = -1;

		int ci=0;

		
		for (j = 0; j <sample.length; j++) {
			if (sample[j].wnum > model.NUM_FEATURES) {
				//System.out.println(doc.fvec.words[j].wnum+" is set to 0");
				System.err.println("wnum is over NUM_FEATURES");
				return null;
				//words[j].wnum = 0;
			}
		}
		
		Word[] fvec;
	
		for (ci = 0; ci < posslabels.length; ci++) {
			y.first_class = posslabels[ci].first_class;
			y.second_class = posslabels[ci].second_class;
			y.third_class = posslabels[ci].third_class;
			//System.err.println(" label y.first_class:"+y.first_class+" y.second_class:"+y.second_class+" y.third_class:"+y.third_class);
			fvec = psi(sample, y);
	
			score = classify_example(fvec);	
			//System.err.println("y.first_class:"+y.first_class+" y.second_class:"+y.second_class+" y.third_class:"+y.third_class+" score:"+score);
			if ((bestscore < score) || first) {
				bestscore = score;
				bestfirst = y.first_class;
				bestsecond = y.second_class;
				bestthird = y.third_class;
				first = false;
			}
		}
      
		y.first_class = bestfirst;
		y.second_class = bestsecond;
		y.third_class = bestthird;
		
		return y;
	}

	@Override
	public Word[] psi(Word[] sample, Label y) {

		//System.err.println("in psi");
		Word[] fvec=new Word[sample.length*3];;
		for(int i=0;i<sample.length;i++)
		{
		  fvec[i]=copy_word(sample[i]);
		  fvec[i+sample.length]=copy_word(sample[i]);
		  fvec[i+sample.length*2]=copy_word(sample[i]);
		  
		  fvec[i].wnum+=(y.first_class-1)*model.NUM_WORDS;
		  fvec[i+sample.length].wnum+=((y.second_class-1)*model.NUM_WORDS+model.NUM_FIRST_CLASS*model.NUM_WORDS);
		  fvec[i+sample.length*2].wnum+=((y.third_class-1)*model.NUM_WORDS+model.NUM_FIRST_CLASS*model.NUM_WORDS+model.NUM_SECOND_CLASS*model.NUM_WORDS);
		}
		/*
		System.out.println();
		for(int i=0;i<fvec.length;i++)
		{
			System.out.print(fvec[i].wnum+":"+fvec[i].weight+" ");
		}
		System.out.println();
        */
		return fvec;
	}
	
	@Override
	public String getCateName(Label y) {
		String cate_name = "";
		String key=y.first_class+"|"+y.second_class+"|"+y.third_class;
		
		//System.out.println("key:"+key);
		
		if(label_names.containsKey(key))
		{
			cate_name=label_names.get(key)+"";
		}
		else
		{
			cate_name = "NA";
		}

		return cate_name;
	}
	
	public void readPossLabels(String input_file)
	{
		try{
		//BufferedReader br=new BufferedReader(new FileReader("genlabeldict_mul.txt"));
		InputStream model_is = this.getClass().getResourceAsStream(
				"/" + input_file);
		InputStreamReader model_isr = new InputStreamReader(model_is);

		BufferedReader br = new BufferedReader(model_isr);
		ArrayList<String> pls=new ArrayList<String>();
		String line="";
		while((line=br.readLine())!=null)
		{
			if(SSO.tioe(line))
			{
				continue;
			}
			line=line.trim();
			pls.add(line);
		}
		
		logger.info("pls.size:"+pls.size());
		
		posslabels=new Label[pls.size()];
		
		Label tlabel=new Label();
		String[] fields=null;
		String labelStr="";
		String[] labels=null;
		for(int i=0;i<pls.size();i++)
		{
			line=pls.get(i);
			fields=line.split("\t");
			if(fields.length!=2)
			{
				continue;
			}
			
			labelStr=fields[0];
			if(SSO.tioe(labelStr))
			{
				continue;
			}
			labelStr=labelStr.trim();
			labels=labelStr.split("\\|");
			if(labels.length!=3)
			{
				continue;
			}
			tlabel=new Label();
			tlabel.first_class=Integer.parseInt(labels[0]);
			tlabel.second_class=Integer.parseInt(labels[1]);
			tlabel.third_class=Integer.parseInt(labels[2]);
			posslabels[i]=tlabel;
		}
		
		br.close();
		model_isr.close();
		model_is.close();
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	@Override
	public HashMap getIndexLabelFromStream(String input_file) {

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
				item=item.trim();
				
				seg_arr = item.split("\t");
				if (seg_arr.length != 2) {
					continue;
				}
				label = seg_arr[1].trim();
				index_str = seg_arr[0].trim();

				if (!(SSO.tnoe(index_str))) {
					continue;
				}

				if (!(SSO.tnoe(label))) {
					continue;
				}

				//System.err.println("index_str:"+index_str+" label:"+label);

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

		ClassifierLayerThree cf = new ClassifierLayerThree();
		String text="凤凰网 凤凰网是中国领先的综合门户网站，提供含文图音视频的全方位综合新闻资讯、深度访谈、观点评论、财经产品、互动应用、分享社区等服务，同时与凤凰无线、凤凰宽频形成动，为全球主流华人提供互联网、无线通信、电视网三网融合无缝衔接的新媒体优质体验。";
		System.out.println("cate:"+cf.cate(text));

		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		
		String line="";
		while((line=br.readLine())!=null)
		{
			if(SSO.tioe(line))
			{
				continue;
			}
			line=line.trim();
			System.out.println("cate:"+cf.cate(line));
			if(line.equals("quit"))
			{
				break;
			}
			System.out.println("input a title");
		}
	}

}
