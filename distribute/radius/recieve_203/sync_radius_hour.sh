#!/bin/sh

#upload data
Usage(){
        echo "$0 <yesterday|20130710>"
}

if [ $# -ne 1 ]; then
        Usage
        exit -1;
fi

day=$1

if [ $1 == "yesterday" ]; then
        day=`date -d"yesterday" +%Y%m%d`
fi
sday=0
if [ $1 == "today" ]; then
        sday=`date  +%Y%m%d`    
        day=`date  -d"1 hour ago" +%Y-%m-%d-%H`
        hour=`date  +%H`
fi
echo $day

lastfile=radiusInfo_$day.log
echo $lastfile
scp  -P 9777 /home/lq/logs/$lastfile 192.168.1.106:/home/radius_data/$sday
