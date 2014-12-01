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
                /*
                $jbkw_local_merge_dir="/tmp/jbkw_".$this->day."_merge_".rand();
                $jbkw_preli_output="/user/nstat/".$this->day."/jbkw/preli_output";
                $jbkw_video_output="/user/nstat/".$this->day."/jbkw/video_output";
                $this->jbkw_prepare($jbkw_local_merge_dir); 
                $this->jbkw_preli_step($jbkw_local_merge_dir,$jbkw_preli_output);
                $this->jbkw_video_cate($jbkw_preli_output,$jbkw_video_output);
               */
               //$jbkw_hdfs_kwl_file="/user/nstat/".$this->day."/jbkw/kwl_sort";
               $hdfs_se_sort_keywords_merge_links="/user/nstat/".$this->day."/se_baidu/sort_keywords_merge_links";
               //$this->jbkw_sortkeywords($hdfs_se_sort_keywords_merge_links);
               $jbkw_local_merge_dir="/tmp/jbkw_".$this->day."_merge_".rand();
               //$this->jbkw_simple_prepare($hdfs_se_sort_keywords_merge_links,$jbkw_local_merge_dir);
               $jbkw_preli_output="/user/nstat/".$this->day."/jbkw/preli_output";
               $jbkw_video_output="/user/nstat/".$this->day."/jbkw/video_output";
              // $this->jbkw_preli_step($jbkw_local_merge_dir,$jbkw_preli_output);
              // $this->jbkw_video_cate($jbkw_preli_output,$jbkw_video_output);
               $jbkw_sort_nv_output="/user/nstat/".$this->day."/jbkw/sort_nv_output";
               $this->merge_nv($jbkw_preli_output,$jbkw_video_output,$jbkw_sort_nv_output);               

	}

	function execute(){

	}

	function clean(){
	}

        function jbkw_sortkeywords($hdfs_se_sort_keywords_merge_links)
        {
                
                $hdfs_se_keywords="/user/nstat/".$this->day."/se_baidu/keywords";
                $hdfs_se_sort_keywords="/user/nstat/".$this->day."/se_baidu/sort_keywords";
                 
                $ret=$this->ha_utils->remove_hdfs($hdfs_se_sort_keywords);
                $cmd = "jar jbkw.jar BKWSortKeyWordsMR $this->day $hdfs_se_keywords $hdfs_se_sort_keywords";
                
                $ret = $this->ha_utils->run_raw($cmd)   ;
                if($ret == false){
                        $this->error("Can not sort keywords:".$this->day);
                        return false;
                }
               

                $hdfs_se_links="/user/nstat/".$this->day."/se_baidu/links";
                $hdfs_se_merge_links="/user/nstat/".$this->day."/se_baidu/merge_links";
                
                $ret=$this->ha_utils->remove_hdfs($hdfs_se_merge_links);
                $cmd = "jar jbkw.jar BKWMergeLinksMR $this->day $hdfs_se_links $hdfs_se_merge_links";
                $ret = $this->ha_utils->run_raw($cmd)   ;
                if($ret == false){
                        $this->error("Can not merge links:".$this->day);
                        return false;
		}
                
		$keywords_temp_hive="jbkw_keywords_temp_".$this->day."_".rand();
		$links_temp_hive="jbkw_links_temp_".$this->day."_".rand();
		$create_tb="CREATE EXTERNAL TABLE $keywords_temp_hive (keyword string,pvs string) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n' LOCATION '$hdfs_se_sort_keywords'";		
		$ret = $this->ha_utils->hive_proc($create_tb);	
		if($ret == false){		
			$this->error("Can not create hive external table using input data");
			return false;		
		}

                $create_tb="CREATE EXTERNAL TABLE $links_temp_hive (keyword string,urls_info string) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n' LOCATION '$hdfs_se_merge_links'";
                $ret = $this->ha_utils->hive_proc($create_tb);
                if($ret == false){
                        $this->error("Can not create hive external table using input data");
                        return false;
                }
    
               // $hdfs_se_sort_keywords_merge_links="/user/nstat/".$this->day."/se_baidu/sort_keywords_merge_links";           
               $merge_table=" INSERT OVERWRITE DIRECTORY '$hdfs_se_sort_keywords_merge_links' SELECT a.keyword,a.pvs,b.urls_info FROM  $keywords_temp_hive a JOIN $links_temp_hive b ON a.keyword=b.keyword where b.keyword IS NOT NULL and a.keyword IS NOT NULL and a.pvs IS NOT NULL and cast(a.pvs as int)>0 and a.pvs IS NOT NULL and b.urls_info IS NOT NULL Order by cast(a.pvs as int)desc ";
                
               //$merge_table=" INSERT OVERWRITE DIRECTORY '$hdfs_se_sort_keywords_merge_links' SELECT a.keyword FROM $keywords_temp_hive a";
                 
                $ret = $this->ha_utils->hive_proc($merge_table);
                if($ret == false){
                        $this->error("Can not create merge two hive table");
                        return false;
                }

                $this->ha_utils->remove_htb($links_temp_hive);
                $this->ha_utils->remove_htb($keywords_temp_hive);
                /*
                $this->ha_utils->remove_hdfs($jbkw_hdfs_kwl_file);
                $cmd = "jar jbkw.jar BKWSortKWL $this->day $hdfs_se_sort_keywords_merge_links $jbkw_hdfs_kwl_file";
                $ret = $this->ha_utils->run_raw($cmd);
                if($ret == false){
                        $this->error("Can not sort kwl:".$this->day);
                        return false;
                }               
                */




	}



        function jbkw_simple_prepare($hdfs_se_sort_keywords_merge_links,$jbkw_local_merge_dir)
        {
                
                $local_se_kwl="/tmp/jbkw_".$this->day."_kwl_".rand();
                $htl_cmd_one=" fs -get ".$hdfs_se_sort_keywords_merge_links." ".$local_se_kwl;
               
                $ret = $this->ha_utils->run_raw($htl_cmd_one);
                if($ret == false){
                        $this->error("Can not load kwl:".$this->day);
                        return false;
                }

                /*
                system("head -n 10000 ".$local_se_kwl." >> ".$jbkw_local_merge_dir);
                if((!empty($local_se_kwl))&&(strstr($local_se_kwl,"/tmp/jbkw")!=false))
                {
                        system("rm -rf ".$local_se_kwl);
                }
               */

               
                require_once('jbkw_preprocess.php');
                $jb_pr=new jbkw_preprocess();

                $jb_pr->selectTopKwl($local_se_kwl,$jbkw_local_merge_dir);


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

	function merge_nv($jbkw_preli_output,$jbkw_video_output,$jbkw_sort_nv_output)
	{
		$this->ha_utils->remove_hdfs($jbkw_sort_nv_output);
		$cmd = " jar jbkw.jar BKWMergeNVMR $this->day $jbkw_preli_output $jbkw_video_output $jbkw_sort_nv_output"; 
   
		$ret = $this->ha_utils->run_raw($cmd);
		if($ret == false){
			$this->error("Can not finish merge novel and video:".$this->day);
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


$jbkw_t = new jbkw_app($day,true);
$jbkw_t->main();

?>

