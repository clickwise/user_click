<?php

$stdin = fopen('php://stdin','r');
if(false===$stdin)
{
   fprintf(STDERR,"can not open stdin \n");
   exit(1);
}
$line="";
$oldtr_file="";
$newtr_file="";
$newte_file="";
$trainSam_file="";
$tr_hosts="";
$te_hosts="";
while(!feof($stdin))
{
   $line=trim(fgets($stdin,4096*64));
   if(strlen($line)>0)
   { 
     $seg_arr=split("[ \t]",$line);
    for($i=0;$i<count($seg_arr);$i++)
    {
//     printf("seg_arr[%d]=%s \n",$i,$seg_arr[$i]);
    }
    if(count($seg_arr)!=6)
    {
     echo "输入词典路径和host文件路径,中间用空格分开\n";
    }
    else
    {
     $oldtr_file=$seg_arr[0];
     $newtr_file=$seg_arr[1];
     $newte_file=$seg_arr[2];
     $trainSam_file=$seg_arr[3];
     $tr_hosts=$seg_arr[4];
     $te_hosts=$seg_arr[5];
    }
   }
} 

//echo "dict_file:".$dict_file."\n";
//echo "nout_file:".$nout_file."\n";


$ts_in=fopen($trainSam_file,'r');
$ts_i=1;
$lines_host=array();
$ts_host="";
while(!feof($ts_in))
{
  $line=trim(fgets($ts_in,4096*64));
//  $seg_arr=split("[ \t]",$line);
//  if(count($seg_arr)<2)
//  {
 //    $ts_i++;
  //   continue;
 // }
  $ts_host=$line;
  if(!isset($lines_host[$ts_i]))
  {
   $lines_host[$ts_i]=$ts_host;
   $ts_i++;
  }
}

fclose($ts_in);
$word_dict=array();
$word_i=1;

$old_in=fopen($oldtr_file,'r');
$newtr_out=fopen($newtr_file,'w');
$newte_out=fopen($newte_file,'w');
$tr_host_out=fopen($tr_hosts,'w');
$te_host_out=fopen($te_hosts,'w');
$ran_num=rand(0,20);
$oi=1;
while(!feof($old_in))
{
  $line=trim(fgets($old_in,4096*16));
  if(strlen($line)>2)
 {
  $line=$line."\n";
  $ran_num=rand(0,20);
  if($ran_num>14)
  {
   fwrite($newte_out,$line);
   fwrite($te_host_out,$lines_host[$oi]."\n");
   $oi++;
  }
  else
  {
   fwrite($newtr_out,$line);
   fwrite($tr_host_out,$lines_host[$oi]."\n");
   $oi++;
  }
 }
}


fclose($old_in);
fclose($tr_host_out);
fclose($te_host_out);
fclose($newtr_out);
fclose($newte_out); 
?>
