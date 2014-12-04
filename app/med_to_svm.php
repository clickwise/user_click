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
 //  $ncount=count($seg_arr)-1;
 $newL="";
 //  $newL=$newL.$ncount." ".$line."\n";
//   echo $newL;
  for($j=1;$j<count($seg_arr);$j++)
  {
    $newL=$newL.$seg_arr[$j]." ";
  }
  echo $newL."\n";
} 

?>
