#!/bin/sh
ps ax |grep java |grep QueryEasyServer | awk '{print $1}' |xargs kill -9
