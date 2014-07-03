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

                /*
		$user_se_keywords_day="/user/nstat/$this->day/user_se_keywords_day";
		$user_se_keywords_day_seg="/user/nstat/$this->day/user_admatch/user_se_keywords_day_seg";
		$this->seg_keywords($user_se_keywords_day,$user_se_keywords_day_seg);   

		$user_se_keywords_day_wordstatis="/user/nstat/$this->day/user_admatch/user_se_keywords_day_wordstatis";
		$this->seg_word_statis($user_se_keywords_day_seg,$user_se_keywords_day_wordstatis);           

		$user_se_keywords_day_wordstatis_tl="/user/nstat/$this->day/user_admatch/user_se_keywords_day_wordstatis_tl";
		$this->seg_word_tl($user_se_keywords_day_wordstatis,$user_se_keywords_day_wordstatis_tl);

		$user_se_keywords_day_wordstatis_keys="/user/nstat/$this->day/user_admatch/user_se_keywords_day_wordstatis_keys";
		$this->seg_word_keys($user_se_keywords_day_wordstatis_tl,$user_se_keywords_day_wordstatis_keys);       

                $user_se_keywords_day_wordstatis_swap="/user/nstat/$this->day/user_admatch/user_se_keywords_day_wordstatis_swap";
                $this->seg_word_swap($user_se_keywords_day_wordstatis_keys,$user_se_keywords_day_wordstatis_swap);
                */


                /*
                $user_host_day="/user/nstat/$this->day/user_host_day";
                $user_host_day_wordstatis="/user/nstat/$this->day/user_admatch/user_host_day_wordstatis";

                $this->hostStatis($user_host_day,$user_host_day_wordstatis);
                */
              
                /*
                $user_cate_day_raw="/user/nstat/$this->day/user_cate_day_raw";
                $user_cate_day_word_statis="/user/nstat/$this->day/user_admatch/user_cate_day_word_statis";

                $this->userCateStatis($user_cate_day_raw,$user_cate_day_word_statis);  
                */

                $user_attrloc_day="/user/nstat/$this->day/user_attrloc_day";  
                $user_attrloc_day_wordstatis="/user/nstat/$this->day/user_admatch/user_attrloc_day_wordstatis";

                $this->attrlocStatis($user_attrloc_day,$user_attrloc_day_wordstatis);
                
                


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
			$this->error("Can not finish seg keywords:".$this->day);
			return false;
		}

		return $ret;
	}   


	function seg_word_statis($user_se_keywords_day_seg,$user_se_keywords_day_wordstatis)
	{
		$this->ha_utils->remove_hdfs($user_se_keywords_day_wordstatis);

		$cmd = " jar smart_hadoop.jar cn.clickwise.user_click.seg.LineWordCountMR 4 3 $user_se_keywords_day_seg $user_se_keywords_day_wordstatis";

		$ret = $this->ha_utils->run_raw($cmd);
		if($ret == false){
			$this->error("Can not finish word_statis:".$this->day);
			return false;
		}

		return $ret;

	}       


	function seg_word_tl($user_se_keywords_day_wordstatis,$user_se_keywords_day_wordstatis_tl) 
	{
		$this->ha_utils->remove_hdfs($user_se_keywords_day_wordstatis_tl);

		$cmd = " jar smart_hadoop.jar cn.clickwise.user_click.seg.FieldProcessMR 4 1 $user_se_keywords_day_wordstatis $user_se_keywords_day_wordstatis_tl STR2LONGTIME";

		$ret = $this->ha_utils->run_raw($cmd);
		if($ret == false){
			$this->error("Can not finish word_statis_tl:".$this->day);
			return false;
		}

		return $ret;

	}


	function seg_word_keys($user_se_keywords_day_wordstatis_tl,$user_se_keywords_day_wordstatis_keys)
	{
		$this->ha_utils->remove_hdfs($user_se_keywords_day_wordstatis_keys);

		$cmd = " jar smart_hadoop.jar cn.clickwise.user_click.seg.FieldProcessMR 4 2 $user_se_keywords_day_wordstatis_tl $user_se_keywords_day_wordstatis_keys STRMAP BD_KEYS";

		$ret = $this->ha_utils->run_raw($cmd);
		if($ret == false){
			$this->error("Can not finish word_statis_keys:".$this->day);
			return false;
		}

		return $ret;

	}

        function seg_word_swap($user_se_keywords_day_wordstatis_keys,$user_se_keywords_day_wordstatis_swap)
        {
                $this->ha_utils->remove_hdfs($user_se_keywords_day_wordstatis_swap);

                $cmd = " jar smart_hadoop.jar cn.clickwise.user_click.seg.DoubleFieldProcessMR 4 1 2 $user_se_keywords_day_wordstatis_keys $user_se_keywords_day_wordstatis_swap FIELDSWAP";

                $ret = $this->ha_utils->run_raw($cmd);
                if($ret == false){
                        $this->error("Can not finish word_statis_swap:".$this->day);
                        return false;
                }

                return $ret;
        }

        function hostStatis($user_host_day,$user_host_day_wordstatis)
        {
                $this->ha_utils->remove_hdfs($user_host_day_wordstatis);

                $cmd = " jar smart_hadoop.jar cn.clickwise.user_click.seg.LineProcessMR 20 $user_host_day $user_host_day_wordstatis MERGE";

                $ret = $this->ha_utils->run_raw($cmd);
                if($ret == false){
                        $this->error("Can not finish host word statis:".$this->day);
                        return false;
                }

                return $ret;

        }

        function userCateStatis($user_cate_day_raw,$user_cate_day_word_statis)
        {
                $this->ha_utils->remove_hdfs($user_cate_day_word_statis);

              
                $cmd = " jar smart_hadoop.jar cn.clickwise.user_click.seg.LineProcessMR 20 $user_cate_day_raw $user_cate_day_word_statis USERCATEMERGE";

                $ret = $this->ha_utils->run_raw($cmd);
                if($ret == false){
                        $this->error("Can not finish user cate statis:".$this->day);
                        return false;
                }

                return $ret;

        }

        function attrlocStatis($user_attrloc_day,$user_attrloc_day_wordstatis)
        {
               $user_attrloc_day_mid="/user/nstat/$this->day/user_admatch/user_attrloc_day_mid"; 
               /*
               $this->ha_utils->remove_hdfs($user_attrloc_day_mid);
               $cmd = " jar smart_hadoop.jar cn.clickwise.user_click.seg.LineWordCountMR 5 4 $user_attrloc_day  $user_attrloc_day_mid";
                
               $ret = $this->ha_utils->run_raw($cmd);
               if($ret == false){
                        $this->error("Can not finish word statis:".$this->day);
                        return false;
               }
               */
          

               $this->ha_utils->remove_hdfs($user_attrloc_day_wordstatis);
               $cmd = " jar smart_hadoop.jar cn.clickwise.user_click.seg.LineProcessMR 20 $user_attrloc_day_mid $user_attrloc_day_wordstatis USERATTRMERGE";
                
               $ret = $this->ha_utils->run_raw($cmd);
               if($ret == false){
                        $this->error("Can not finish user attr statis:".$this->day);
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

