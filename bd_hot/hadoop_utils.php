<?php

date_default_timezone_set("Asia/Shanghai");

/**
 * Hadoop utilities in PHP
 *
 * 2013-07-27
 * Created by Alan Shu (shuchengchun@clickwise.cn)
 * 
 */
class hadoop_utils{
	var $HADOOP="/home/hadoop/hadoop/bin/hadoop";
        var $HIVE="/home/hadoop/hive/bin/hive -e ";
	var $debug_mode = true;
	
	function debug($msg){
		if($this->debug_mode)
			fprintf(STDERR,"%s\n",$msg);
	}

	function error($msg){
		fprintf(STDERR,"%s\n",$msg);
	}

	/**
	 * Load the local directory into HDFS
	 *
	 * @param	input	the local directory for holding data as input
	 * @param	output	the HDFS directory for storing the data
	 *
	 * @return	true if succeed, false otherwise
	 */
	function load_raw_hdfs($input,$output){
		$runtime=date("YmdHis");
		$this->debug("$runtime:Load data from local $input to HDFS:$output");

		//system("find $input -size -100 | xargs rm -rf");
		system($this->HADOOP." fs -rmr $output");
		system($this->HADOOP." fs -mkdir $output");
		system($this->HADOOP." fs -put $input/* $output", $ret);

		if($ret !== 0){
			$this->error("$runtime: Can not load data to HDFS:$input=>$output");
			return false;
		}
		return true;
	}

	/**
	 * Load the local directory into HDFS
	 *
	 * @param	input	the local directory for holding data as input
	 * @param	output	the HDFS directory for storing the data
	 *
	 * @return	true if succeed, false otherwise
	 */
	function load_raw_hdfs_ex($input,$output){
		$runtime=date("YmdHis");
		$this->debug("$runtime:Load data from local $input to HDFS:$output");

		system($this->HADOOP." fs -rmr $output");
		system($this->HADOOP." fs -mkdir $output");
		system($this->HADOOP." fs -put $input $output", $ret);

		if($ret !== 0){
			$this->error("$runtime: Can not load data to HDFS:$input=>$output");
			return false;
		}
		return true;
	}

	/**
	 * create hdfs directory 
         *
	 * @param	input	the hdfs to remove
	 *
	 * @return	true if succeed, false otherwise
         */
	function create_hdfs_dir($input){
		$runtime=date("YmdHis");
		system($this->HADOOP." fs -mkdir $input", $ret);

		if($ret !== 0){
			$this->error("$runtime: Can not create HDFS directory:$input");
			return false;
		}
		return true;
	}

	/**
	 * Remove hdfs directory 
	 *
	 * @param	input	the hdfs to remove
	 *
	 * @return	true if succeed, false otherwise
	 */
	function remove_hdfs($input){
		$runtime=date("YmdHis");
		$this->debug("$runtime: Remove HDFS $input");

		system($this->HADOOP." fs -rmr $input",$ret);

		if($ret !== 0){
			$this->error("$runtime: Can not remove HDFS:$input");
			return false;
		}
		return true;
	}

	/**
	 * Drop Hive table
	 *
	 * @param	htb	Host hive table to delete
	 *
	 * @return 	true if succeed, false otherwise
	 */
	function remove_htb($htb){
		$runtime=date("YmdHis");
		$deltb = "drop table $htb";
		$ret = $this->hive_proc($deltb);
		if($ret !== 0){
			$this->error("$runtime: Can not remove Hive table:$htb");
			return false;
		}
		return true;
	}

	/**
	 * Run Raw Map-Reduce Program
	 *
	 * @param	cmd	the command to run the raw map-reduce program
	 *
	 * @return	true if succeed, false otherwise
	 */
	function run_cmd($cmd1){
		$runtime=date("YmdHis");
		$this->debug("$runtime: Run hadoop cmd : hadoop $cmd1 ");

		$cmd= "$this->HADOOP $cmd1";
		
		system($cmd, $ret);
		if( $ret !==0){
			$this->error("$runtime: Can not run hadoop cmd: $cmd ");
			return false;
		}

		return true;
	}

	/**
	 * Run Raw Map-Reduce Program
	 *
	 * @param	cmd	the command to run the raw map-reduce program
	 *
	 * @return	true if succeed, false otherwise
	 */
	function run_raw($cmd1){
		$runtime=date("YmdHis");
		$this->debug("$runtime: Run Raw Map Reduce : hadoop $cmd1 ");

		$cmd= "$this->HADOOP $cmd1";
		
		system($cmd, $ret);
		if( $ret !==0){
			$this->error("$runtime: Can not run Raw Map-Reduce: hadoop $cmd1 ");
			return false;
		}

		return true;
	}

	/**
	 * Run the Map-Reduce Program
	 *
	 * @param	PROG	the name the program,used for tracking only
	 * @param	INPUT	the HDFS Directory of INPUT
	 * @param	OUTPUT	the HDFS Directory of OUTPUT
	 * @param	MAPPER	the Mapper command
	 * @param	REDUCER	the Reducer command
	 * @param	FILES	the files included in MAPPER and REDUCER
	 * @param	MAP_TASKS	the number of map tasks
	 * @param	RED_TASKS	the number of reduce tasks
	 *
	 * @return	true if succeed, false otherwise
	 */
	function run_mr($PROG,$INPUT,$OUTPUT,$MAPPER,$REDUCER,$FILES,$MAP_TASKS=300,$RED_TASKS=1){
		$runtime=date("YmdHis");
		$this->debug("$runtime: Run Map Reduce $PROG ");

		if($REDUCER == ""){
			$REDUCER_OPT= " ";
			$RED_TASKS = 0;
		}else{
			$REDUCER_OPT= " -reducer '$REDUCER' ";
		}
	
		$FILE_OPT="";
		foreach($FILES as $file){
			$FILE_OPT.=" -file $file ";
		}
		system($this->HADOOP." fs -rmr $OUTPUT");

		if($RED_TASKS <0)
			$red_tasks = " ";
		else
			 $red_tasks=" -jobconf mapred.reduce.tasks=$RED_TASKS ";
			

		$cmd= "$this->HADOOP jar /home/hadoop/hadoop/contrib/streaming/hadoop-streaming-0.20.203.0.jar streaming \
			 -input $INPUT \
			 -output $OUTPUT \
			 -mapper '$MAPPER' \
			 $REDUCER_OPT \
			 $FILE_OPT \
			 -jobconf mapred.job.name='$PROG' \
			 -jobconf mapred.map.tasks=$MAP_TASKS \
			 $red_tasks ";
		
		system($cmd, $ret);
		if( $ret !==0){
			$this->error("$runtime: Can not run Map-Reduce $PROG ");
			return false;
		}

		return true;
	}

	/**
	 * hive_proc
	 * 
	 * @param	hsql	the hive QL statement
	 *
	 * @return	true if succeed, false otherwise
	 */
	function hive_proc($hql){
		$runtime=date("YmdHis");
		$this->debug("$runtime: Run hive :$hql ");

		system($this->HIVE. "\"$hql\"", $ret);
		if($ret !== 0){
			$this->error("$runtime: Failed to execute hive sql:\n $hql");
			return false;
		}
		return true;
	}

	/**
	 * load data in HDFS into hive table
	 * 
	 * @param	src_file_path	the source HDFS file path
	 * @param	hive_table	the hive table to load 
	 * @param	create_hsql	the hql for creating the hive table
	 * @param	$partion_st	the partition statment if applies
	 *
	 * @return	true if succeed, false otherwise
	 */
	function load_into_hive($src_file_path, $hive_table,$create_hsql="",$partition_st=""){
		if(strlen($create_hsql)>0)
			$this->hive_proc($create_hsql);

		$hsql = "load data inpath '$src_file_path' overwrite into table $hive_table ";
		if(strlen($partition_st)>0)
			$hsql .= "partition($partition_st);";
		$this->debug($hsql);
		return $this->hive_proc($hsql);
	}
}
?>
