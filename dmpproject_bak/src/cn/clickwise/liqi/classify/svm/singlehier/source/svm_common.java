package cn.clickwise.liqi.classify.svm.singlehier.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.clickwise.liqi.time.utils.TimeOpera;

public class svm_common {

	public static final String VERSION = "V6.20";
	public static final String VERSION_DATE = "14.08.08";

	public static final short LINEAR = 0;
	public static final short POLY = 1;
	public static final short RBF = 2;
	public static final short SIGMOID = 3;
	public static final short CUSTOM = 4;
	public static final short GRAM = 5;

	public static final short CLASSIFICATION = 1;
	public static final short REGRESSION = 2;
	public static final short RANKING = 3;
	public static final short OPTIMIZATION = 4;

	public static final int MAXSHRINK = 50000;

	public static final String INST_NAME = "Multi-Class SVM";

	public static final String INST_VERSION = "V2.20";

	public static final String INST_VERSION_DATE = "14.08.08";

	/**
	 * default precision for solving the optimization problem
	 */
	public static final double c = 0.1;

	/**
	 * default loss rescaling method: 1=slack_rescaling, 2=margin_rescaling
	 */
	public static final short DEFAULT_RESCALING = 2;

	/**
	 * default loss function:
	 */
	public static final int DEFAULT_LOSS_FCT = 0;

	/**
	 * default optimization algorithm to use:
	 */
	public static final int DEFAULT_ALG_TYPE = 4;

	/**
	 * store Psi(x,y) once instead of recomputing it every time:
	 */
	public static final int USE_FYCACHE = 0;

	/**
	 * decide whether to evaluate sum before storing vectors in constraint
	 * cache: 0 = NO, 1 = YES (best, if sparse vectors and long vector lists), 2
	 * = YES (best, if short vector lists), 3 = YES (best, if dense vectors and
	 * long vector lists)
	 */
	public static final short COMPACT_CACHED_VECTORS = 2;

	/**
	 * minimum absolute value below which values in sparse vectors are rounded
	 * to zero. Values are stored in the FVAL type defined in svm_common.h
	 * RECOMMENDATION: assuming you use FVAL=float, use 10E-15 if
	 * COMPACT_CACHED_VECTORS is 1 10E-10 if COMPACT_CACHED_VECTORS is 2 or 3
	 */
	public static final double COMPACT_ROUNDING_THRESH = 10E-15;

	public static int kernel_cache_statistic = 0;
	public static int verbosity = 0;

	public static int read_totdocs;
	public static int read_totwords;
	public static int read_max_docs;
	public static int read_max_words_doc;

	public static double read_doc_label;
	public static int read_queryid;
	public static int read_slackid;
	public static double read_costfactor;
	public static int read_wpos;
	public static String read_comment;

	public static double[] read_target = null;

	public static SVECTOR create_svector(WORD[] words, String userdefined,
			double factor) {
		SVECTOR vec;
		int fnum, i;

		fnum = 0;

		vec = new SVECTOR();
		vec.words = new WORD[words.length];

		for (i = 0; i < words.length; i++) {
			vec.words[i] = words[i];
		}

		vec.twonorm_sq = -1;

		if (userdefined != null) {
			vec.userdefined = userdefined;
		} else {
			vec.userdefined = null;
		}

		vec.kernel_id = 0;
		vec.next = null;
		vec.factor = factor;

		return vec;
	}

	public static DOC create_example(int docnum, int queryid, int slackid,
			double costfactor, SVECTOR fvec) {

		DOC example = new DOC();

		example.docnum = docnum;
		example.kernelid = docnum;
		example.queryid = queryid;
		example.slackid = slackid;
		example.costfactor = costfactor;

		example.fvec = fvec;

		return example;
	}

	public static double kernel(KERNEL_PARM kernel_parm, DOC a, DOC b) {
		// System.out.println("in kernel");
		double sum = 0;
		SVECTOR fa, fb;
		if (kernel_parm.kernel_type == GRAM) {
			System.out.println("kernel_type:" + GRAM);
			if ((a.kernelid >= 0) && (b.kernelid >= 0)) {

				return kernel_parm.gram_martix.element[Math.max(a.kernelid,
						b.kernelid)][Math.min(a.kernelid, b.kernelid)];
			} else {
				return 0;
			}
		}
		// System.out.println("fa pro");
		for (fa = a.fvec; fa != null; fa = fa.next) {
			for (fb = b.fvec; fb != null; fb = fb.next) {

				if (fa.kernel_id == fb.kernel_id) {
					if (sum > 0)
						System.out.println("sum:" + sum);
					sum += fa.factor * fb.factor
							* single_kernel(kernel_parm, fa, fb);
				}
			}
		}

		return sum;
	}

	public static double single_kernel(KERNEL_PARM kernel_parm, SVECTOR a,
			SVECTOR b) {
		kernel_cache_statistic++;

		switch (kernel_parm.kernel_type) {
		case LINEAR:
			// System.out.println("liner kernel y");
			return sprod_ss(a, b);
		case POLY:
			return Math.pow(kernel_parm.coef_lin * sprod_ss(a, b)
					+ kernel_parm.coef_const, kernel_parm.poly_degree);
		case RBF:
			if (a.twonorm_sq < 0) {
				a.twonorm_sq = sprod_ss(a, a);
			} else if (b.twonorm_sq < 0) {
				b.twonorm_sq = sprod_ss(b, b);
			}
			return Math.exp(-kernel_parm.rbf_gamma
					* (a.twonorm_sq - 2 * sprod_ss(a, b) + b.twonorm_sq));
		case SIGMOID:
			return Math.tanh(kernel_parm.coef_lin * sprod_ss(a, b)
					+ kernel_parm.coef_const);
		case CUSTOM:
			return kernel.custom_kernel(kernel_parm, a, b);
		default:
			System.out.println("Error: Unknown kernel function");
			System.exit(1);

		}

		return 0;
	}

	public static double sprod_ss(SVECTOR a, SVECTOR b) {
		double sum = 0;
		WORD[] ai, bj;
		ai = a.words;
		bj = b.words;

		int i = 0;
		int j = 0;
		/*
		 * for(int k=0;k<ai.length;k++) {
		 * System.out.print(k+":"+ai[k].wnum+":"+ai[k].weight+" "); }
		 * System.out.println(); for(int k=0;k<bj.length;k++) {
		 * System.out.print(k+":"+bj[k].wnum+":"+bj[k].weight+" "); }
		 */
		// System.out.println();
		// System.out.println("ai.length:"+ai.length+" bj.length:"+bj.length);
		while ((i < ai.length) && (j < bj.length)) {
			if (ai[i].wnum > bj[j].wnum) {
				j++;
			} else if (ai[i].wnum < bj[j].wnum) {
				i++;
			} else {

				sum += ai[i].weight * bj[j].weight;
				i++;
				j++;
			}
		}

		return sum;
	}

	public static void clear_nvector(double[] vec, int n) {
		int i;
		for (i = 0; i <= n; i++) {
			vec[i] = 0;
		}
	}

	public static double[] create_nvector(int n) {
		double[] vector;
		vector = new double[n + 1];
		return vector;
	}

	public static void add_vector_ns(double[] vec_n, SVECTOR vec_s,
			double faktor) {
		WORD[] ai;
		ai = vec_s.words;
		for (int i = 0; i < ai.length; i++) {
			vec_n[ai[i].wnum] += (faktor * ai[i].weight);
		}
	}

	public static double sprod_ns(double[] vec_n, SVECTOR vec_s) {
		double sum = 0;
		WORD[] ai;
		ai = vec_s.words;
		for (int i = 0; i < ai.length; i++) {
			sum += (vec_n[ai[i].wnum] * ai[i].weight);
		}

		return sum;
	}

	public static void mult_vector_ns(double[] vec_n, SVECTOR vec_s,
			double faktor) {
		WORD[] ai;
		ai = vec_s.words;
		for (int i = 0; i < ai.length; i++) {
			vec_n[ai[i].wnum] *= (faktor * ai[i].weight);
		}

	}

	public static double get_runtime() {
		int c = (int) TimeOpera.getCurrentTimeLong();
		double hc = 0;
		hc = ((double) c) * 10;
		return hc;
	}

	public static double model_length_s(MODEL model)
	/* compute length of weight vector */
	{
		int i, j;
		double sum = 0, alphai;
		DOC supveci;
		KERNEL_PARM kernel_parm = model.kernel_parm;

		for (i = 1; i < model.sv_num; i++) {
			alphai = model.alpha[i];
			supveci = model.supvec[i];
			for (j = 1; j < model.sv_num; j++) {
				sum += alphai * model.alpha[j]
						* kernel(kernel_parm, supveci, model.supvec[j]);
			}
		}
		return (Math.sqrt(sum));
	}

	public static void set_learning_defaults(LEARN_PARM learn_parm,
			KERNEL_PARM kernel_parm) {
		learn_parm.type = CLASSIFICATION;
		learn_parm.predfile = "trans_predictions";
		learn_parm.alphafile = "";
		learn_parm.biased_hyperplane = 1;
		learn_parm.sharedslack = 0;
		learn_parm.remove_inconsistent = 0;
		learn_parm.skip_final_opt_check = 0;
		learn_parm.svm_maxqpsize = 10;
		learn_parm.svm_newvarsinqp = 0;
		learn_parm.svm_iter_to_shrink = -9999;
		learn_parm.maxiter = 100000;
		learn_parm.kernel_cache_size = 40;
		learn_parm.svm_c = 0.0;
		learn_parm.eps = 0.1;
		learn_parm.transduction_posratio = -1.0;
		learn_parm.svm_costratio = 1.0;
		learn_parm.svm_costratio_unlab = 1.0;
		learn_parm.svm_unlabbound = 1E-5;
		learn_parm.epsilon_crit = 0.001;
		learn_parm.epsilon_a = 1E-15;
		learn_parm.compute_loo = 0;
		learn_parm.rho = 1.0;
		learn_parm.xa_depth = 0;
		kernel_parm.kernel_type = LINEAR;
		kernel_parm.poly_degree = 3;
		kernel_parm.rbf_gamma = 1.0;
		kernel_parm.coef_lin = 1;
		kernel_parm.coef_const = 1;
		kernel_parm.custom = "empty";
	}

	public static DOC[] read_documents(String docfile, double[] label) {
		String line, comment;
		PrintWriter pw = null;
		FileWriter fw = null;
		DOC[] docs;
		try {
			fw = new FileWriter(new File("log2.txt"));
			pw = new PrintWriter(fw);
		} catch (Exception e) {
		}
		int dnum = 0, wpos, dpos = 0, dneg = 0, dunlab = 0, queryid, slackid, max_docs;
		int max_words_doc, ll;
		double doc_label, costfactor;
		FileReader fr = null;
		BufferedReader br = null;

		if (verbosity >= 1) {
			System.out.println("Scanning examples...");
		}

		nol_ll(docfile); /* scan size of input file */
		read_max_words_doc += 2;
		read_max_docs += 2;
		if (verbosity >= 1) {
			System.out.println("done\n");
		}
		try {
			fr = new FileReader(new File(docfile));
			br = new BufferedReader(fr);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		docs = new DOC[read_max_docs]; /* feature vectors */
		// for(int k=0;k<read_docs.length;k++)
		// {
		// read_docs[k]=ps.getDOC();
		// }
		WORD[] words;
		label = new double[read_max_docs]; /* target values */
		// System.out.println("docs length:"+docs.length);
		words = new WORD[read_max_words_doc + 10];
		for (int j = 0; j < words.length; j++) {
			words[j] = new WORD();
		}
		if (verbosity >= 1) {
			System.out.println("Reading examples into memory...");
		}
		dnum = 0;
		read_totwords = 0;
		try {
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.charAt(0) == '#')
					continue; /* line contains comments */
				// System.out.println(line);

				if ((words = parse_document(line, read_max_words_doc)) == null) {
					System.out.println("\nParsing error in line " + dnum
							+ "!\n" + line);
					// System.exit(1);
					continue;
				}
				label[dnum] = read_doc_label;
				/* printf("docnum=%ld: Class=%f ",dnum,doc_label); */
				if (read_doc_label > 0)
					dpos++;
				if (read_doc_label < 0)
					dneg++;
				if (read_doc_label == 0)
					dunlab++;
				if ((read_wpos > 1)
						&& ((words[read_wpos - 2]).wnum > read_totwords))
					read_totwords = words[read_wpos - 2].wnum;

				docs[dnum] = create_example(dnum, read_queryid, read_slackid,
						read_costfactor,
						create_svector(words, read_comment, 1.0));
				pw.println("docs dnum[" + dnum + "]"
						+ docs[dnum].fvec.words.length);
				for (int k = 0; k < 100; k++) {
					pw.print(" " + docs[dnum].fvec.words[k].wnum + ":"
							+ docs[dnum].fvec.words[k].weight);
				}
				// System.out.println();
				/* printf("\nNorm=%f\n",((*docs)[dnum]->fvec)->twonorm_sq); */
				dnum++;
				if (verbosity >= 1) {
					if ((dnum % 100) == 0) {
						// System.out.println(dnum+"..");
					}
				}
			}

			fr.close();
			br.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (verbosity >= 1) {
			System.out.println("OK. (" + dnum + " examples read)\n");
		}
		read_totdocs = dnum;
		read_target = label;
		return docs;
	}

	public static WORD[] parse_document(String line, int max_words_doc) {
		int wpos = 0, pos;
		int wnum;
		double weight;
		String featurepair, junk;

		WORD[] words;
		words = new WORD[max_words_doc];
		for (int k = 0; k < words.length; k++) {
			words[k] = new WORD();
		}
		read_queryid = 0;
		read_slackid = 0;
		read_costfactor = 1;

		pos = 0;
		read_comment = "";
		String dline = "";
		/* printf("Comment: '%s'\n",(*comment)); */
		if (line.indexOf("#") > 0) {
			read_comment = line.substring(line.indexOf("#") + 1, line.length());
			dline = line.substring(0, line.indexOf("#"));
		} else {
			dline = line;
		}

		dline = dline.trim();
		wpos = 0;

		String[] seg_arr = dline.split("\\s+");
		// System.out.println("seg_arr.length:"+seg_arr.length);
		if (seg_arr.length < 1) {
			return null;
		}
		read_doc_label = Double.parseDouble(seg_arr[0]);

		String wstr = "";
		String pstr = "";
		String sstr = "";
		for (int i = 1; i < seg_arr.length; i++) {
			wstr = seg_arr[i].trim();
			if (wstr.indexOf(":") < 0) {
				continue;
			}
			pstr = wstr.substring(0, wstr.indexOf(":"));
			sstr = wstr.substring(wstr.indexOf(":") + 1, wstr.length());
			pstr = pstr.trim();
			sstr = sstr.trim();
			if (pstr.equals("qid")) {
				read_queryid = Integer.parseInt(sstr);
			} else if (pstr.equals("sid")) {
				read_slackid = Integer.parseInt(sstr);
			} else if (pstr.equals("cost")) {
				read_costfactor = Double.parseDouble(sstr);
			} else if (Pattern.matches("[\\d]+", pstr)) {
				words[wpos].wnum = Integer.parseInt(pstr);
				words[wpos].weight = Double.parseDouble(sstr);
				wpos++;
			}
		}

		words[wpos].wnum = 0;
		read_wpos = wpos + 1;
		// System.out.println("wpos:"+read_wpos);
		return words;
	}

	public static void nol_ll(String input_file) {

		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(new File(input_file));
			br = new BufferedReader(fr);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		String line = "";
		int temp_docs = 0;
		int temp_words = 0;
		String[] seg_arr = null;
		try {
			while ((line = br.readLine()) != null) {
				line = line.trim();
				temp_docs++;
				seg_arr = line.split("\\s+");
				if (seg_arr.length > temp_words) {
					temp_words = seg_arr.length;
				}
			}

			read_max_docs = temp_docs;
			read_max_words_doc = temp_words;
			System.out.println("read_max_docs:" + read_max_docs);
			System.out.println("read_max_words_doc:" + read_max_words_doc);

			fr.close();
			br.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static double[] read_alphas(String alphafile, int totdoc)
	/*
	 * reads the alpha vector from a file as written by the write_alphas
	 * function
	 */
	{
		FileReader fr = null;
		BufferedReader br = null;
		double[] alpha = null;
		try {
			fr = new FileReader(new File(alphafile));
			br = new BufferedReader(fr);

			alpha = new double[totdoc];
			int dnum = 0;
			String line = "";

			while ((line = br.readLine()) != null) {
				alpha[dnum] = Double.parseDouble(line);
				dnum++;
			}
			fr.close();
			br.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return (alpha);
	}

	/***************************** IO routines ***************************/

	public static void write_model(String modelfile, MODEL model) {
		FileWriter fw = null;
		PrintWriter pw = null;

		try {
			fw = new FileWriter(new File(modelfile));
			pw = new PrintWriter(fw);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		int j, i, sv_num;
		SVECTOR v;
		MODEL compact_model = null;

		if (verbosity >= 1) {
			System.out.println("Writing model file...");
		}

		/* Replace SV with single weight vector */
		if (false && (model.kernel_parm.kernel_type == LINEAR)) {
			if (verbosity >= 1) {
				System.out.println("(compacting...");
			}
			compact_model = compact_linear_model(model);
			model = compact_model;
			if (verbosity >= 1) {
				System.out.println("done)");
			}
		}

		pw.println("SVM-light Version " + VERSION);
		pw.println(model.kernel_parm.kernel_type + " # kernel type");
		pw.println(model.kernel_parm.poly_degree + " # kernel parameter -d ");
		pw.println(model.kernel_parm.rbf_gamma + " # kernel parameter -g ");
		pw.println(model.kernel_parm.coef_lin + " # kernel parameter -s ");
		pw.println(model.kernel_parm.coef_const + " # kernel parameter -r ");
		pw.println(model.kernel_parm.custom + "# kernel parameter -u ");
		pw.println(model.totwords + " # highest feature index ");
		pw.println(model.totdoc + " # number of training documents ");

		sv_num = 1;
		for (i = 1; i < model.sv_num; i++) {
			for (v = model.supvec[i].fvec; v != null; v = v.next)
				sv_num++;
		}
		pw.println(sv_num + " # number of support vectors plus 1 \n");
		pw.println(model.b
				+ " # threshold b, each following line is a SV (starting with alpha*y)\n");

		for (i = 1; i < model.sv_num; i++) {
			for (v = model.supvec[i].fvec; v != null; v = v.next) {
				pw.print(model.alpha[i] * v.factor + " ");
				for (j = 0; (v.words[j]).wnum != 0; j++) {
					pw.print((int) (v.words[j]).wnum + ":"
							+ (double) (v.words[j]).weight + " ");
				}
				if (v.userdefined != null)
					pw.print("#" + v.userdefined + "\n");
				else
					pw.print("#\n");
				/*
				 * NOTE: this could be made more efficient by summing the
				 * alpha's of identical vectors before writing them to the file.
				 */
			}
		}

		if (verbosity >= 1) {
			System.out.println("done\n");
		}
	}

	public static MODEL compact_linear_model(MODEL model)
	/*
	 * Makes a copy of model where the support vectors are replaced with a
	 * single linear weight vector.
	 */
	/* NOTE: It adds the linear weight vector also to newmodel->lin_weights */
	/* WARNING: This is correct only for linear models! */
	{
		MODEL newmodel;
		newmodel = new MODEL();
		newmodel = model;
		add_weight_vector_to_linear_model(newmodel);
		newmodel.supvec = new DOC[2];
		newmodel.alpha = new double[2];
		newmodel.index = null; /* index is not copied */
		newmodel.supvec = null;
		newmodel.alpha[0] = 0.0;
		newmodel.supvec[1] = create_example(
				-1,
				0,
				0,
				0,
				create_svector_n(newmodel.lin_weights, newmodel.totwords, null,
						1.0));
		newmodel.alpha[1] = 1.0;
		newmodel.sv_num = 2;

		return (newmodel);
	}

	public static void add_weight_vector_to_linear_model(MODEL model)
	/* compute weight vector in linear case and add to model */
	{
		int i;
		SVECTOR f;

		model.lin_weights = create_nvector(model.totwords);
		clear_nvector(model.lin_weights, model.totwords);
		for (i = 1; i < model.sv_num; i++) {
			for (f = (model.supvec[i]).fvec; f != null; f = f.next)
				add_vector_ns(model.lin_weights, f, f.factor * model.alpha[i]);
		}
	}

	public static SVECTOR create_svector_n(double[] nonsparsevec,
			int maxfeatnum, String userdefined, double factor) {
		return (create_svector_n_r(nonsparsevec, maxfeatnum, userdefined,
				factor, 0));
	}

	public static SVECTOR create_svector_n_r(double[] nonsparsevec,
			int maxfeatnum, String userdefined, double factor,
			double min_non_zero) {
		SVECTOR vec;
		int fnum, i;

		fnum = 0;
		for (i = 1; i <= maxfeatnum; i++)
			if ((nonsparsevec[i] < -min_non_zero)
					|| (nonsparsevec[i] > min_non_zero))
				fnum++;

		vec = new SVECTOR();
		vec.words = new WORD[fnum + 1];
		fnum = 0;
		for (i = 1; i <= maxfeatnum; i++) {
			if ((nonsparsevec[i] < -min_non_zero)
					|| (nonsparsevec[i] > min_non_zero)) {
				vec.words[fnum].wnum = i;
				vec.words[fnum].weight = nonsparsevec[i];
				fnum++;
			}
		}
		vec.words[fnum].wnum = 0;
		vec.twonorm_sq = -1;

		if (userdefined != null) {
			vec.userdefined = userdefined;
		} else
			vec.userdefined = null;

		vec.kernel_id = 0;
		vec.next = null;
		vec.factor = factor;
		return (vec);
	}

	public static void copyright_notice() {
		System.out
				.println("\nCopyright: Thorsten Joachims, thorsten@joachims.org");
		System.out
				.println("This software is available for non-commercial use only. It must not");
		System.out
				.println("be modified and distributed without prior permission of the author.");
		System.out
				.println("The author is not responsible for implications from the use of this");
		System.out.println("software.\n\n");
	}

	public static boolean check_learning_parms(LEARN_PARM learn_parm,
			KERNEL_PARM kernel_parm) {
		System.out.println("check_learning_parms");
		if ((learn_parm.skip_final_opt_check != 0)
				&& (kernel_parm.kernel_type == LINEAR)) {
			System.out
					.println("\nIt does not make sense to skip the final optimality check for linear kernels.\n\n");
			learn_parm.skip_final_opt_check = 0;
		}
		if ((learn_parm.skip_final_opt_check != 0)
				&& (learn_parm.remove_inconsistent != 0)) {
			System.out
					.println("\nIt is necessary to do the final optimality check when removing inconsistent \nexamples.\n");
			return false;
		}
		if ((learn_parm.svm_maxqpsize < 2)) {
			System.out
					.println("\nMaximum size of QP-subproblems not in valid range: "
							+ learn_parm.svm_maxqpsize + " [2..]\n");
			return false;
		}
		if ((learn_parm.svm_maxqpsize < learn_parm.svm_newvarsinqp)) {
			System.out.println("\nMaximum size of QP-subproblems ["
					+ learn_parm.svm_maxqpsize
					+ "] must be larger than the number of\n");
			System.out.println("new variables [" + learn_parm.svm_newvarsinqp
					+ "] entering the working set in each iteration.\n");
			return false;
		}
		if (learn_parm.svm_iter_to_shrink < 1) {
			System.out
					.println("\nMaximum number of iterations for shrinking not in valid range: "
							+ learn_parm.svm_iter_to_shrink + " [1,..]\n");
			return false;
		}
		if (learn_parm.svm_c < 0) {
			System.out
					.println("\nThe C parameter must be greater than zero!\n\n");
			return false;
		}
		if (learn_parm.transduction_posratio > 1) {
			System.out
					.println("\nThe fraction of unlabeled examples to classify as positives must\n");
			System.out.println("be less than 1.0 !!!\n\n");
			return false;
		}
		if (learn_parm.svm_costratio <= 0) {
			System.out
					.println("\nThe COSTRATIO parameter must be greater than zero!\n\n");
			return false;
		}
		if (learn_parm.epsilon_crit <= 0) {
			System.out
					.println("\nThe epsilon parameter must be greater than zero!\n\n");
			return false;
		}
		if (learn_parm.rho < 0) {
			System.out
					.println("\nThe parameter rho for xi/alpha-estimates and leave-one-out pruning must\n");
			System.out
					.println("be greater than zero (typically 1.0 or 2.0, see T. Joachims, Estimating the\n");
			System.out
					.println("Generalization Performance of an SVM Efficiently, ICML, 2000.)!\n\n");
			return false;
		}
		if ((learn_parm.xa_depth < 0) || (learn_parm.xa_depth > 100)) {
			System.out
					.println("\nThe parameter depth for ext. xi/alpha-estimates must be in [0..100] (zero\n");
			System.out
					.println("for switching to the conventional xa/estimates described in T. Joachims,\n");
			System.out
					.println("Estimating the Generalization Performance of an SVM Efficiently, ICML, 2000.)\n");
		}
		System.out.println("true");
		return true;
	}

	public static SVECTOR shift_s(SVECTOR a, int shift) {
		SVECTOR vec;
		WORD[] sum;
		WORD[] sumi;
		WORD[] ai;
		int veclength;
		String userdefined = "";

		ai = a.words;
		veclength = ai.length;
		sum = new WORD[veclength];
		sumi = new WORD[veclength];
		ai = a.words;
		for (int i = 0; i < ai.length; i++) {
			sumi[i] = ai[i];
			sumi[i].wnum += shift;
		}

		if (a.userdefined != null) {

		}

		vec = svm_common.create_svector_shallow(sum, userdefined, a.factor);

		return vec;
	}

	public static SVECTOR create_svector_shallow(WORD[] words,
			String userdefined, double factor) {
		SVECTOR vec;
		vec = new SVECTOR();
		vec.words = words;
		vec.twonorm_sq = -1;
		vec.userdefined = userdefined;
		vec.kernel_id = 0;
		vec.next = null;
		vec.factor = factor;

		return vec;
	}

	public static SVECTOR add_list_ss(SVECTOR a) {
		return (add_list_ss_r(a, 0));
	}

	public static SVECTOR add_list_ss_r(SVECTOR a, double min_non_zero)
	/*
	 * computes the linear combination of the SVECTOR list weighted by the
	 * factor of each SVECTOR
	 */
	{
		SVECTOR oldsum, sum, f;
		WORD[] empty = new WORD[2];

		if (a == null) {
			empty[0].wnum = 0;
			sum = create_svector(empty, null, 1.0);
		} else if ((a != null) && (a.next != null)) {
			sum = smult_s(a, a.factor);
		} else {
			sum = multadd_ss_r(a, a.next, a.factor, a.next.factor, min_non_zero);
			for (f = a.next.next; f != null; f = f.next) {
				oldsum = sum;
				sum = multadd_ss_r(oldsum, f, 1.0, f.factor, min_non_zero);
			}
		}
		return (sum);
	}

	public static SVECTOR smult_s(SVECTOR a, double factor)
	/* scale sparse vector a by factor */
	{
		SVECTOR vec;
		WORD[] sum, sumi;
		WORD[] ai;
		int veclength;
		String userdefined = null;

		ai = a.words;
		veclength = ai.length;

		sum = new WORD[veclength];
		sumi = new WORD[veclength];
		ArrayList<WORD> wordlist = new ArrayList<WORD>();
		ai = a.words;
		WORD temp_word = null;
		for (int i = 0; i < veclength; i++) {
			temp_word = ai[i];
			temp_word.weight *= factor;
			if (temp_word.weight != 0) {
				wordlist.add(temp_word);
			}
		}

		sumi = new WORD[wordlist.size()];

		for (int i = 0; i < wordlist.size(); i++) {
			sumi[i] = wordlist.get(i);
		}

		if (a.userdefined != null) {
			userdefined = a.userdefined;
		}

		vec = create_svector_shallow(sumi, userdefined, 1.0);
		return (vec);
	}

	/**
	 * compute fa*a+fb*b of two sparse vectors Note: SVECTOR lists are not
	 * followed, but only the first SVECTOR is used
	 * 
	 * @param a
	 * @param b
	 * @param fa
	 * @param fb
	 * @param min_non_zero
	 * @return
	 */
	public static SVECTOR multadd_ss_r(SVECTOR a, SVECTOR b, double fa,
			double fb, double min_non_zero) {
		SVECTOR vec;
		WORD[] sum, sumi;
		WORD[] ai, bj;
		int veclength;
		double weight;

		ai = a.words;
		bj = b.words;
		veclength = 0;
		int i = 0;
		int j = 0;

		while (i < ai.length && j < bj.length) {
			if (ai[i].wnum > bj[j].wnum) {
				veclength++;
				j++;
			} else if (ai[i].wnum < bj[j].wnum) {
				veclength++;
				i++;
			} else {
				veclength++;
				i++;
				j++;
			}
		}

		while (j < bj.length) {
			veclength++;
			j++;
		}

		while (i < ai.length) {
			veclength++;
			i++;
		}
		veclength++;

		sum = new WORD[veclength];
		sumi = sum;
		ai = a.words;
		bj = b.words;
		i = 0;
		j = 0;
		int s = 0;
		while (i < ai.length && j < bj.length) {
			if (ai[i].wnum > bj[j].wnum) {
				sumi[s] = bj[j];
				sumi[s].weight *= fb;
				s++;
				j++;
			} else if (ai[i].wnum < bj[j].wnum) {
				sumi[s] = ai[i];
				sumi[s].weight *= fa;
				s++;
				i++;
			} else {
				weight = fa * (double) ai[i].weight + fb
						* (double) bj[j].weight;
				if ((weight < -min_non_zero) || (weight > min_non_zero)) {
					sumi[s].wnum = ai[i].wnum;
					sumi[s].weight = weight;
					s++;
				}
				i++;
				j++;
			}
		}
		while (j < bj.length) {
			sumi[s] = bj[j];
			sumi[s].weight *= fb;
			s++;
			j++;
		}
		while (i < ai.length) {
			sumi[s] = ai[i];
			sumi[s].weight *= fa;
			s++;
			i++;
		}

		if (true) { /* potentially this wastes some memory, but saves malloc'ing */
			vec = create_svector_shallow(sumi, null, 1.0);
		} else { /* this is more memory efficient */
			vec = create_svector(sumi, null, 1.0);

		}
		return (vec);
	}
	
public static double classify_example(MODEL model, DOC ex) 
    /* classifies one example */
{
     int i;
     double dist;

     if((model.kernel_parm.kernel_type == LINEAR) && (model.lin_weights!=null))
     {
       //printf("model kernel type is LINEAR and lin_weights \n");
       return(classify_example_linear(model,ex));
     }	   
     dist=0;
    for(i=1;i<model.sv_num;i++) {  
      dist+=kernel(model.kernel_parm,model.supvec[i],ex)*model.alpha[i];
    }
    return(dist-model.b);
}

public static double classify_example_linear(MODEL model, DOC ex) 
/* classifies example for linear kernel */

/* important: the model must have the linear weight vector computed */
/* use: add_weight_vector_to_linear_model(&model); */


/* important: the feature numbers in the example to classify must */
/*            not be larger than the weight vector!               */
{
   double sum=0;
   SVECTOR f;

   for(f=ex.fvec;f!=null;f=f.next)  
    sum+=f.factor*sprod_ns(model.lin_weights,f);
    return(sum-model.b);
}


public static SVECTOR copy_svector(SVECTOR vec)
{
  SVECTOR newvec=null;
  if(vec!=null) {
    newvec=create_svector(vec.words,vec.userdefined,vec.factor);
    newvec.kernel_id=vec.kernel_id;
    newvec.next=copy_svector(vec.next);
  }
  return(newvec);
}

public static void append_svector_list(SVECTOR a, SVECTOR b) 
/* appends SVECTOR b to the end of SVECTOR a. */
{
   SVECTOR f;

   for(f=a;f.next!=null;f=f.next);  /* find end of first vector list */
     f.next=b;                   /* append the two vector lists */
}

public static SVECTOR add_ss(SVECTOR a, SVECTOR b) 
/* compute the sum a+b of two sparse vectors */
/* Note: SVECTOR lists are not followed, but only the first
SVECTOR is used */
{
     return(multadd_ss_r(a,b,1.0,1.0,0));
}


public static MODEL copy_model(MODEL model)
{
	  MODEL newmodel;
	  int  i;

	  newmodel=new MODEL();
	  newmodel.supvec=new DOC[model.sv_num];
	  newmodel.alpha=new double[model.sv_num];

	  newmodel.index = null; /* index is not copied */
	  newmodel.supvec[0] = null;//为什么第0个设置为 null?
	  newmodel.alpha[0] = 0;
	  for(i=1;i<model.sv_num;i++) {
	    newmodel.alpha[i]=model.alpha[i];
	    newmodel.supvec[i]=svm_common.create_example(model.supvec[i].docnum,
					       model.supvec[i].queryid,0,
					       model.supvec[i].costfactor,
					       svm_common.copy_svector(model.supvec[i].fvec));
	  }
	  if(model.lin_weights!=null) {
	    newmodel.lin_weights = new double[model.totwords+1];
	    for(i=0;i<model.totwords+1;i++) 
	      newmodel.lin_weights[i]=model.lin_weights[i];
	  }
	  return(newmodel);
	}


}
