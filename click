#!/bin/bash
#echo $#
#echo $1
source /etc/profile
COMMAND=$1
shift
JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.65.x86_64
JAVA=$JAVA_HOME/bin/java
CLASSPATH=${CLASSPATH}run_lib/feathouse.jar

if [ "$COMMAND" = "seg" ] ; then
  CLASS='cn.clickwise.clickad.seg.Segmenter'
elif [ "$COMMAND" = "tag" ] ; then
  CLASS='cn.clickwise.clickad.tag.PosTagger'
elif [ "$COMMAND" = "key" ] ; then
  CLASS='cn.clickwise.clickad.keyword.KeyExtract'
elif [ "$COMMAND" = "classify" ] ; then
  CLASS='cn.clickwise.clickad.classify.ClassifierUseText'
elif [ "$COMMAND" = "dmpInquiry" ] ; then
  CLASS='cn.clickwise.clickad.feathouse.ScheduleTask'
fi

#echo $CLASS
#echo $CLASSPATH
#echo $@

exec "$JAVA" -Xmx2000m -classpath "$CLASSPATH" $CLASS "$@"
