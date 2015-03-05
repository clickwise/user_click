#!/bin/sh
while [ 1 ]
do
echo "restart radius_client :`date` " >> runtime.log
java -cp radius.jar:. cn.clickwise.clickad.radiusClient.EasyRadiusClient
#sleep 2 
done
