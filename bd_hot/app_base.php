<?php

/**
 * app_base
 *
 *
 * Created by Alan Shu (shuchengchun@clickwise.cn), 20130728
 */
abstract class app_base{
	var $input ;
	var $output;
	var $name;
	var $debug_mode = true;
	function __construct($debug_mode=false){
		$this->debug_mode = $debug_mode;
	}
	function __destruct(){
	}

	function error($msg){
		fprintf(STDERR,"%s\n",$msg);
	}

	function debug($msg){
		if($this->debug_mode)
		fprintf(STDOUT,"%s\n",$msg);
	}
	function info($msg){
		fprintf(STDOUT,"%s\n",$msg);
	}
	function main(){
		$ret = $this->prepare();
		if($ret == false)
			return false;
		$ret = $this->execute();
		if($ret == false)
			return false;
		$this->clean();

		return $ret;
	}
	abstract function prepare();
	abstract function execute();
	abstract function clean();
};

?>
