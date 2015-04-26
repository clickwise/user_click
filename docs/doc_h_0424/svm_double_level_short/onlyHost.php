<?php
  $stdin=fopen('php://stdin','r');
  if(false===$stdin)
  {
   fprintf(STDERR,"can not open stdin \n");
   exit(1);
  }
  $line="";
  $host="";
  while(!feof($stdin))
  {
    $line=trim(fgets($stdin,4096*64));
    $seg_arr=split("[ \t]",$line);
    $host=$seg_arr[0]; 
    echo $host."\n";
  }
?>
