#!/bin/bash
#echo $#
#echo $1
COMMAND=$1
shift

JAVA=$JAVA_HOME/bin/java
CLASSPATH=${CLASSPATH}out/user_click.jar

if [ "$COMMAND" = "seg" ] ; then
  CLASS='cn.clickwise.clickad.seg.Segmenter'
elif [ "$COMMAND" = "tag" ] ; then
  CLASS='cn.clickwise.clickad.tag.PosTagger'
elif [ "$COMMAND" = "key" ] ; then
  CLASS='cn.clickwise.clickad.keyword.KeyExtract'
fi

#echo $CLASS
#echo $CLASSPATH
#echo $@

exec "$JAVA"  -classpath "$CLASSPATH" $CLASS "$@"
