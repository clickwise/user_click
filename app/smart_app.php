<?php

date_default_timezone_set("Asia/Shanghai");
/**
 *dns.php 
 */

require_once "app_base.php";
require_once "hadoop_utils.php";


class smart_app extends app_base{
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
 
           /**filter cookied record**/
           $input_hdfs="/user/clickwise/nstat/$this->day/raw";
           $output_hdfs="/user/clickwise/nstat_cookie/$this->day";
           
           /***过滤出有cookie的记录*******/   
           ////$this->filter_cookie($input_hdfs,$output_hdfs);
        
           $cookie_output="/user/clickwise/nstat_only_cookie/$this->day";
  
           /***只把cookie过滤出来*******/
           ////$this->filter_only_cookie($output_hdfs,$cookie_output);

           $nana_output="/user/clickwise/nstat_analysis/$this->day";
           /***分析link里的文字，并分词*******/
           ////$this->nstat_analysis($output_hdfs,$nana_output);

           $host_cate_hdfs="/user/clickwise/host_cate_redis/20140612";
           $host_title_seg_mark_hdfs="/user/clickwise/host_title_seg_mark/20140612";
           $nstat_user_info="/user/clickwise/nstat_user_info/$this->day";
           $this->user_info_completion($host_cate_hdfs,$host_title_seg_mark_hdfs,$nana_output,$nstat_user_info);
            
	}

	function execute(){

	}

	function clean(){
	}



        function filter_cookie($input_hdfs,$output_hdfs)
        {
           $this->ha_utils->remove_hdfs($output_hdfs);

           $cmd = " jar smart_hadoop.jar cn.clickwise.smartjobs.FiltCookiedRecord $this->day $input_hdfs $output_hdfs";
          
           $ret = $this->ha_utils->run_raw($cmd);
           if($ret == false){
                   $this->error("Can not find the fields from dns log file:".$this->day);
                   return false;
           }

           
      
           return $ret;
        }   


       function filter_only_cookie($input_hdfs,$output_hdfs)
       {
           $this->ha_utils->remove_hdfs($output_hdfs);

           $cmd = " jar smart_hadoop.jar cn.clickwise.smartjobs.FiltCookie $this->day $input_hdfs $output_hdfs";

           $ret = $this->ha_utils->run_raw($cmd);
           if($ret == false){
                   $this->error("Can not find the fields from dns log file:".$this->day);
                   return false;
           }



           return $ret;
       }
        

       function nstat_analysis($input_hdfs,$output_hdfs)
       {
           $this->ha_utils->remove_hdfs($output_hdfs);

           $cmd = " jar smart_hadoop.jar cn.clickwise.smartjobs.NstatUserAnalysis $this->day $input_hdfs $output_hdfs";

           $ret = $this->ha_utils->run_raw($cmd);
           if($ret == false){
                   $this->error("Can not find the fields from dns log file:".$this->day);
                   return false;
           }



           return $ret;
       }


       function user_info_completion($host_cate_hdfs,$host_title_seg_mark_hdfs,$nstat_analysis_hdfs,$nstat_user_info)
       {
          $this->ha_utils->remove_hdfs($nstat_user_info);

          $cmd="jar smart_hadoop.jar cn.clickwise.smartjobs.NstatUserInfoCompletion $this->day $host_cate_hdfs $host_title_seg_mark_hdfs $nstat_analysis_hdfs $nstat_user_info";

          $ret = $this->ha_utils->run_raw($cmd);
          if($ret == false){
                   $this->error("Can not finish user info completion:".$this->day);
                   return false;
          }

          return $ret;
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


$smart_t = new smart_app($day,true);
$smart_t->main();

?>

