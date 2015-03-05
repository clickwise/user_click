#!/bin/sh

ps ax |grep sh |grep restart_radius_queue | awk '{print $1}' |xargs kill -9
echo "there"
sleep 2
ps ax |grep java |grep RemoteResolve | awk '{print $1}' |xargs kill -9
sleep 2
echo "here"
java -cp radiusReform.jar:. cn.clickwise.clickad.radiusReform.RemoteResolve > /home/lq/log.txt 2>&1 &

#while [ 1 ]
#do
#echo "restart query_server :`date` " >> runtime.log
#java -cp radiusReform.jar:. cn.clickwise.clickad.radiusReform.RemoteResolve > /home/lq/log.txt 2>&1 &
#sleep 2
#done
