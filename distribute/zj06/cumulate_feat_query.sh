#!/bin/sh
while [ 1 ]
do
echo "restart cumulate :`date` " >> runtime.log
java -cp feathouse.jar cn.clickwise.clickad.feathouse.CumulateQueryManager
ps ax |grep java |grep CumulateQueryManager | awk '{print $1}' |xargs kill -9
#sleep 2
done
