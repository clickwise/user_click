#!/bin/bash
java -cp user_click.jar cn.clickwise.clickad.server.NLPServer -p 9009  -d so_dict.txt -t 0 > log_seg.txt 2>&1 &
java -cp user_click.jar cn.clickwise.clickad.server.NLPServer -p 9010   -t 1 > log_tag.txt 2>&1 &
java -cp user_click.jar cn.clickwise.clickad.server.NLPServer -p 9011   -t 2 > log_key.txt 2>&1 &
java -Xmx10000m -cp user_click.jar cn.clickwise.clickad.classify_pattern.ClassifyPatternServer -p 9012 -d so_dict.txt > log_tbcate.txt 2>&1 &
