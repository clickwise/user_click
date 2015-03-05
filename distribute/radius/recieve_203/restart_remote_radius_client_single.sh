#!/bin/bash
ps ax |grep java |grep RemoteRadiusReformNoRecursive | awk '{print $1}' |xargs kill -9
