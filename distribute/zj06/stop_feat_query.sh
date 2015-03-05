#!/bin/sh
ps ax |grep java |grep CumulateQueryManager | awk '{print $1}' |xargs kill -9
