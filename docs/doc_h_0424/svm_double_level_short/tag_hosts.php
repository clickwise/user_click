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
$host_file="";
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
    if(count($seg_arr)!=3)
    {
     echo "输入未标记样本，host文件和输出标记样本路径,中间用空格分开\n";
    }
    else
    {
     $oldtr_file=$seg_arr[0];
     $newtr_file=$seg_arr[2];
     $host_file=$seg_arr[1];
    }
   }
} 


$old_in=fopen($oldtr_file,'r');
$new_out=fopen($newtr_file,'w');
$host_in=fopen($host_file,'r');


$tag_dict=array();
$host="";
$tag="";
while(!feof($host_in))
{
 $line=trim(fgets($host_in,4096*64));
 $seg_arr=split("[ \t]",$line);
 if(count($seg_arr)<2)
 {
  continue;
 }
 $host=$seg_arr[1];
 $tag=$seg_arr[0];
 if(!isset($tag_dict[$host]))
 {
  $tag_dict[$host]=$tag;
 }
}



$host="";
$tr_line="";
while(!feof($old_in))
{
 $line=trim(fgets($old_in,4096*64));
 $seg_arr=split("[ \t]",$line);
 if(count($seg_arr)<5)
 {
   continue;
 } 

 $host=$seg_arr[0];
 $tag=$seg_arr[1];
 $tag=$tag_dict[$host];
 $tr_line="";
 $tr_line=$tr_line.$tag." ";
 for($i=1;$i<count($seg_arr);$i++)
 {
  $tr_line=$tr_line.$seg_arr[$i]." ";
 }
 $tr_line=$tr_line."\n";
 fwrite($new_out,$tr_line);
 fwrite($host_out,$host."\n");

}
fclose($new_out);
fclose($host_in);
fclose($old_in);
?>
