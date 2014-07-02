#!/bin/bash


if [ $# -lt 3 ]
then
  echo "Usage <day> <part> <hdfs>"
  exit
else
  echo ""
fi


DAY=$1
PART=$2
HDFS=$3

echo "day:"$DAY
echo "part:"$PART
echo "hdfs:"$HDFS

hadoop fs -rmr $HDFS"/_SUCCESS"
hadoop fs -rmr $HDFS"/_logs"
hive -e "load data inpath '$HDFS' overwrite into table user_admatch partition(dt=$DAY,dp='$PART');"
