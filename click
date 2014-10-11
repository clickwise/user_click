#!/bin/bash
#echo $#
#echo $1
COMMAND=$1
shift

JAVA=$JAVA_HOME/bin/java
CLASSPATH=${CLASSPATH}out/feathouse.jar

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
