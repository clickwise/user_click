#!/bin/sh
while [ 1 ]
do
echo "restart query_easy_server :`date` " >> runtime.log
java -cp feathouse.jar cn.clickwise.clickad.feathouse.QueryEasyServer 6579
#ps ax |grep java |grep ConcurrentProcessRadiusClient | awk '{print $1}' |xargs kill -9
#sleep 2
done
