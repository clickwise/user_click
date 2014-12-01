#!/bin/bash

data_date=`date +%Y%m%d`
data_date=20131116
echo $data_date

HADOOP=/home/hadoop/hadoop/bin/hadoop

KEYWORDS_HDFS=/user/nstat/$data_date/se_baidu/keywords
IS_KEYWORDS_EXIST=`$HADOOP fs -ls $KEYWORDS_HDFS`
echo "IS_KEY_EX:"$IS_KEYWORDS_EXIST

if [ $? -ne 1 ];
then 
  echo "exist"
else
  echo "not exist"
fi

