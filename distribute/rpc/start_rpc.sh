#!/bin/sh
while [ 1 ]
do
echo "restart rpc_server :`date` " >> runtime.log
java -cp rpc.jar cn.clickwise.rpc.EasyServer 2733 
ps ax |grep java |grep EasyServer | awk '{print $1}' |xargs kill -9
#sleep 2
done
