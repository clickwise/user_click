<?php
$redis = new Redis();
$redis->connect('192.168.110.186',6379);
$database=10;
$redis->select($database);
$key="www.suning.com";
echo "db_size:".$redis->dbsize()."\n";
$pattern_bh = $redis->get($key);
echo "pattern_bh:".$pattern_bh."  ".strlen($pattern_bh)."\n";
//$redis->set("zhidao.baidu.com","社区");
$pin_key="*";
$ret = $redis->keys($pin_key);
//$redis->set("zhidao.baidu.com","社区");
$line_num=0;
foreach($ret as $kt => $kv)
{


       echo $kt." ".$kv."   ";
    $ra=$redis->get($kv);
    
    
  //  $redis->del($kv);
    
    echo $ra."\n";
    $line_num++;
    if($line_num>100)
      break;

}

?>
