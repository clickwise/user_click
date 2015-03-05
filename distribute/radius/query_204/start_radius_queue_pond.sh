#!/bin/sh
while [ 1 ]
do
echo "restart query_server :`date` " >> runtime.log
java -cp radiusReform.jar:. cn.clickwise.clickad.radiusReform.RemoteResolve
#sleep 2 
done
