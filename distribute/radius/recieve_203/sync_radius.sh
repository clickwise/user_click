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

echo $day
scp -r -P 9777 /home/lq/logs/$day 192.168.1.105:/home/radius_data/
