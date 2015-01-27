#!/bin/bash
ps ax |grep java |grep NLPServer | awk '{print $1}' |xargs kill -9
ps ax |grep java |grep ClassifyPatternServer | awk '{print $1}' |xargs kill -9
