<?php
$stdin=fopen('php://stdin','r');
if(false==$stdin)
{
   fprintf(STDERR,"can not open stdin \n");
   exit(1);
}

$chost="";
$curl="";
$title="";
$pvs="";
$uvs="";
$ips="";

while(!feof($stdin))
{
   $line=trim(fgets($stdin,4096*64));
   $seg_arr=explode("\001",$line);
  // echo "count.seg_arr:".count($seg_arr)."\n";
   if(count($seg_arr)<6)
   {
     continue;
   }
   $chost=trim($seg_arr[0]);
   $curl=trim($seg_arr[1]);
   $title=trim($seg_arr[2]);
   $pvs=trim($seg_arr[3]);
   $uvs=trim($seg_arr[4]);
   $ips=trim($seg_arr[5]);
 
   echo $chost."\t".$curl."\t".base64_decode($title)."\t".$pvs."\t".$uvs."\t".$ips."\n"; 
}

fclose($stdin);



?>
