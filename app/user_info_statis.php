<?php

date_default_timezone_set("Asia/Shanghai");
/**
 *dns.php 
 */

require_once "app_base.php";
require_once "hadoop_utils.php";


class user_info_statis extends app_base{
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
          
           $user_se_keywords_day="/user/nstat/$this->day/user_se_keywords_day";
           $user_se_keywords_day_seg="/user/nstat/$this->day/user_se_keywords_day_seg";
           $this->seg_keywords($user_se_keywords_day,$user_se_keywords_day_seg);   
            
	}

	function execute(){

	}

	function clean(){
	}



        function seg_keywords($user_se_keywords_day,$user_se_keywords_day_seg)
        {
           $this->ha_utils->remove_hdfs($user_se_keywords_day_seg);

           $cmd = " jar smart_hadoop.jar cn.clickwise.user_click.seg.SegMR 4 3 $user_se_keywords_day $user_se_keywords_day_seg";
          
           $ret = $this->ha_utils->run_raw($cmd);
           if($ret == false){
                   $this->error("Can not finish seg keywords log file:".$this->day);
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


$smart_t = new user_info_statis($day,true);
$smart_t->main();

?>

