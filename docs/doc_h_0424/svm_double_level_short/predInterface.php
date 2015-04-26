<?php

$stdin=fopen('php://stdin','r');
if(false==$stdin)
{
   fprintf(STDERR,"can not open stdin \n");
   exit(1);
}

$line="";
$te_file="";
$te_host_file="";
$tag_index_file="";
$prediction_file="";
$new_pre_file="";
$wrong_sts_file="";
while(!feof($stdin))
{
   $line=trim(fgets($stdin,4096*64));  
   $seg_arr=split("[ \t]",$line);
   if(count($seg_arr)!=6)
   {
    echo "args:\n";  
   }
   else
   {
     $te_file=$seg_arr[0];
     $te_host_file=$seg_arr[1];
     $tag_index_file=$seg_arr[2];
     $prediction_file=$seg_arr[3];
     $new_pre_file=$seg_arr[4];
     $wrong_sts_file=$seg_arr[5];
   } 
}

$answer=array();
$te_in=fopen($te_file,'r');
$a_i=1;
$ans_item=1;
while(!feof($te_in))
{
  $line=trim(fgets($te_in,4096*64));
  $seg_arr=split("[ \t]",$line);
  $ans_item=$seg_arr[0];
  if(!isset($answer[$a_i]))
  {
   $answer[$a_i]=$ans_item;
   $a_i++;
  }  
}
echo "ai=".$a_i."\n";
fclose($te_in);

$line_hosts=array();
$te_host_in=fopen($te_host_file,'r');
$h_i=1;
$host_item=1;
while(!feof($te_host_in))
{
  $line=trim(fgets($te_host_in,4096*64));
  if(!isset($line_hosts[$h_i]))
  {
   $line_hosts[$h_i]=$line;
   $h_i++;
  }
}
echo "h_i=".$h_i."\n";
fclose($te_host_in);

$label_hash=array();
$tif_in=fopen($tag_index_file,'r');
$tif_i=1;
while(!feof($tif_in))
{
  $line=trim(fgets($tif_in,4096*64));
  $seg_arr=split("[ \t]",$line);
  if(count($seg_arr)<2)
  {
    continue;
  } 
 
  $tif_i=$seg_arr[1];
  if(!isset($label_hash[$tif_i]))
  {
   $label_hash[$tif_i]=$seg_arr[0];
  }
}

fclose($tif_in);


$pre_in=fopen($prediction_file,'r');
$wrong_sts_out=fopen($wrong_sts_file,'a');
$new_pre_out=fopen($new_pre_file,'w');
$pre_ans="";
$p_i=1;
$wrong_hash=array();
$ns_line="";
$wrong_num=0;
$tot_num=0;
while(!feof($pre_in)&&$p_i<$a_i)
{
  $tot_num++;
  $line=trim(fgets($pre_in,4096*64)); 
  $seg_arr=split("[ \t]",$line);
  if(count($seg_arr)<1)
  {
   continue;
  }
  $pre_ans=$seg_arr[0];
  $ns_line=$line_hosts[$p_i]." ".$label_hash[$pre_ans];
  fwrite($new_pre_out,$ns_line."\n");
  echo "pre_ans=".$pre_ans." answer=".$answer[$p_i]."\n";
  if($pre_ans!=$answer[$p_i])
  {
    $wrong_num++;
    if(!isset($wrong_hash[$answer[$p_i]]))
    {
      $wrong_hash[$answer[$p_i]]=$line_hosts[$p_i]."[".$label_hash[$pre_ans]."] ";
    }
    else
    {
      $wrong_hash[$answer[$p_i]]=$wrong_hash[$answer[$p_i]].$line_hosts[$p_i]."[".$label_hash[$pre_ans]."] ";
    }
  }  
  $p_i++;
}
echo "p_i=".$p_i."\n";
foreach($wrong_hash as $wkey=>$wval)
{
  fwrite($wrong_sts_out,$label_hash[$wkey]." ".$wval."\n");
}
fwrite($wrong_sts_out,"tothostnum:".$tot_num." wronghostnum:".$wrong_num."\n");
fclose($pre_in);
fclose($wrong_sts_out);
?>
