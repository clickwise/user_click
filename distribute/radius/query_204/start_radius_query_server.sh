#!/bin/sh
while [ 1 ]
do
echo "restart query_server :`date` " >> runtime2.log
java -cp radius.jar:. cn.clickwise.clickad.radiusClient.QueryEasyServer 7535
#sleep 2 
done
