#!/bin/bash

echo $1
ANT_BUILD=/usr/local/ant/bin/ant
echo $ANT_BUILD
OPT=1
if [ "$1" = "user_click" ]
then 
 echo "build user_click";
 $ANT_BUILD -buildfile build.xml
else
 OPT=1
fi

if [[ "$1" = "tool" ]]
then
 echo "build tool";
 $ANT_BUILD -buildfile build_tool.xml
else
 OPT=2
fi

if [ $1 = "feathouse" ]
then
 rm -rf feathouse_src/cn
 cd src 
 cp -r --parents cn/clickwise/clickad/feathouse/ ../feathouse_src
 cd ..
 echo "build feathouse";
 $ANT_BUILD -buildfile build_feathouse.xml
else
 OPT=3
fi



