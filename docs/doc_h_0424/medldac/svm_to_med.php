<?php

$stdin = fopen('php://stdin','r');
if(false===$stdin)
{
   fprintf(STDERR,"can not open stdin \n");
   exit(1);
}
$line="";
$newL="";
$ncount=0;
while(!feof($stdin))
{
   $line=trim(fgets($stdin,4096*64));
   $seg_arr=split("[ \t]",$line);
   if(count($seg_arr)<2)
   {
    continue;
   }
   $label=trim($seg_arr[0]);
   if(empty($label))
   {
     continue;
   }
   $word="";
   $wn=0;
   $wline="";
   for($j=1;$j<count($seg_arr);$j++)
   {
    $word=trim($seg_arr[$j]);
    if(!empty($word))
    {
       $wn++;
       $wline=$wline.$word." ";      
    }
  //  echo $seg_arr[$j]."\n";
   }
   $wline=trim($wline);
   echo $wn." ".$label." ".$wline."\n";
 
} 

?>
