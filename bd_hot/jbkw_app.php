<?php

date_default_timezone_set("Asia/Shanghai");
/**
 *jbkw_app.php 
 */

require_once "app_base.php";
require_once "hadoop_utils.php";

class jbkw_app extends app_base{
	var $ha_utils ;
	var $day; 


	function __construct($day, $debug_mode=false){
		parent::__construct($debug_mode);
		$this->day = $day;
		$this->ha_utils = new hadoop_utils();

	}

	function __destruct(){
	}

	function prepare(){
                
                $jbkw_local_merge_dir="/tmp/jbkw_".$this->day."_merge_".rand();
                $jbkw_preli_output="/user/nstat/".$this->day."/jbkw/preli_output";
                $jbkw_video_output="/user/nstat/".$this->day."/jbkw/video_output";
                $this->jbkw_prepare($jbkw_local_merge_dir); 
                $this->jbkw_preli_step($jbkw_local_merge_dir,$jbkw_preli_output);
                $this->jbkw_video_cate($jbkw_preli_output,$jbkw_video_output);

	}

	function execute(){

	}

	function clean(){
	}

	function clk_prepare(){
		$local_input="/home/hadoop/lq/kan114/BaiduHotKeyWords/BKW_Src/JBKW/input/";
		$local_dec_input="/home/hadoop/lq/kan114/BaiduHotKeyWords/BKW_Src/JBKW/temp/conv_input";
		decode_text($local_input,$local_dec_input);
		$hdfs_input="/user/jbkw/".$this->day."/raw_input";
		$hdfs_output="/user/jbkw/".$this->day."/prepare_output_prepare";
		//$this->ha_utils->remove_hdfs($hdfs_input);
		$ret=$this->ha_utils->load_raw_hdfs($local_dec_input,$hdfs_input);
		echo "this_day:".$this->day."\n";
		echo "hdfs_input:".$hdfs_input."\n";
		echo "hdfs_output:".$hdfs_output."\n";

		$ret=$this->ha_utils->remove_hdfs($hdfs_output);
		$cmd = "jar out/jbkw.jar RankUserUrl $this->day $hdfs_input $hdfs_output";
		$ret = $this->ha_utils->run_raw($cmd)   ;
		if($ret == false){
			$this->error("Can not prepare jbkw:".$this->day);
			return false;
		}

		/*
		   $local_tt_input="/home/hadoop/lq/kan114/BaiduHotKeyWords/BKW_Src/JBKW/tt";
		   $hdfs_tt_input="/user/jbkw/".$this->day."/raw_tt_input";
		   $ret=$this->ha_utils->load_raw_hdfs($local_tt_input,$hdfs_tt_input);
		 */

		$hdfs_tt_sec_output="/user/jbkw/".$this->day."/prepare_output_sec_tt";
		$this->ha_utils->remove_hdfs($hdfs_tt_sec_output);
		$cmd = "jar  out/jbkw.jar RankUserUrlSec $this->day $hdfs_output $hdfs_tt_sec_output ";
		$ret = $this->ha_utils->run_raw($cmd);
		if($ret == false){
			$this->error("Can not prepare jbkw sec:".$this->day);
			return false;
		}
	}

	function baidu_prepare(){


	}
        
        function jbkw_prepare($jbkw_local_merge_dir)
        {
                $hdfs_se_keywords="/user/nstat/".$this->day."/se_baidu/keywords";
                $hdfs_se_links="/user/nstat/".$this->day."/se_baidu/links";
                $local_se_keywords="/tmp/jbkw_".$this->day."_keywords_".rand();
                $local_se_links="/tmp/jbkw_".$this->day."_links_".rand();
                $htl_cmd_one=" fs -get ".$hdfs_se_keywords." ".$local_se_keywords;
                $htl_cmd_two=" fs -get ".$hdfs_se_links." ".$local_se_links;
                $ret = $this->ha_utils->run_raw($htl_cmd_one);
                if($ret == false){
                        $this->error("Can not prepare get keywords:".$this->day);
                        return false;
                }

                $ret = $this->ha_utils->run_raw($htl_cmd_two);
                if($ret == false){
                        $this->error("Can not prepare get links:".$this->day);
                        return false;
                }

                require_once('jbkw_preprocess.php');
                $jb_pr=new jbkw_preprocess();

                $jb_pr->merge_keyword_relfiles($local_se_keywords,$local_se_links,$jbkw_local_merge_dir);
        }
	function jbkw_preli_step($jbkw_local_merge_dir,$jbkw_preli_output){
	
                $jbkw_preli_input="/user/nstat/".$this->day."/jbkw/preli_input";
		$ret=$this->ha_utils->load_raw_hdfs($jbkw_local_merge_dir, $jbkw_preli_input);

		$this->ha_utils->remove_hdfs($jbkw_preli_output);
		$cmd = "jar jbkw.jar BKWHotWordsMR $this->day $jbkw_preli_input $jbkw_preli_output";          
		$ret = $this->ha_utils->run_raw($cmd);
		if($ret == false){
			$this->error("Can not finish jbkw preli step:".$this->day);
			return false;
		}
		if((!empty($jbkw_local_merge_dir))&&(strstr($jbkw_local_merge_dir,"/tmp/jbkw")!=false))
		{
			system("rm -rf ".$jbkw_local_merge_dir);
		}

	}

	function jbkw_video_cate($jbkw_preli_output,$jbkw_video_output){

		$this->ha_utils->remove_hdfs($jbkw_video_output);
		$cmd = " jar jbkw.jar VideoCateMR $this->day $jbkw_preli_output $jbkw_video_output ";

		$ret = $this->ha_utils->run_raw($cmd);
		if($ret == false){
			$this->error("Can not finish video cate:".$this->day);
			return false;
		} 

	}

};

function usage(){
	global $argv;
	echo "$argv[0] <yesterday|20130728>\n";
}

if($argc !=2){
	usage();
	exit(-1);
}

$day = $argv[1];
if($day == "yesterday"){
	$day = date("Ymd",strtotime('yesterday')); 
}

function decode_text($input_dir,$output_dir)
{
	if (!file_exists($output_dir)){ 
		mkdir($output_dir);
	}

	$fout=fopen($output_dir."/0000",'w');
	$input_dh=opendir($input_dir);
	while(($input_file=readdir($input_dh))!=false)
	{
		echo "input_file:".$input_file."\n";
		if($input_file!="."&&$input_file!="..")
		{
			$fin=fopen($input_dir."/".$input_file,'r');
			$chost="";
			$curl="";
			$title="";
			$pvs="";
			$uvs="";
			$ips="";
			while(!feof($fin))
			{
				$line=trim(fgets($fin,4096*64));
				$seg_arr=explode("\001",$line);
				// echo "count.seg_arr:".count($seg_arr)."\n";
				if(count($seg_arr)<6)
				{
					continue;
				}
				$chost=trim($seg_arr[0]);
				$curl=trim($seg_arr[1]);
				$title=trim($seg_arr[2]);
				$pvs=trim($seg_arr[3]);
				$uvs=trim($seg_arr[4]);
				$ips=trim($seg_arr[5]);
				fwrite($fout,$chost."\001".$curl."\001".base64_decode($title)."\001".$pvs."\001".$uvs."\001".$ips."\n");

			}

			fclose($fin);   

		}
	}


	fclose($fout);
}

    
$jbkw_t = new jbkw_app($day,true);
$jbkw_t->main();

?>

