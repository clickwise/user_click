user_click
==========
配置运行环境

cat b.txt | click seg dict/all_words.txt 2 1 001 | click tag 2 1 001 | click key 2 1 001 | click classify 2 1 001

make tool:
 ./clean.sh
 ./make.sh tool

make rpc:
 ./clean.sh
 ./make.sh tool
 ./clean.sh
 ./make.sh rpc

make feathouse:
  ./clean.sh
 ./make.sh tool
 ./clean.sh 
 ./make.sh rpc
 ./clean.sh
 ./make.sh feathouse

make user_click:
 ./clean.sh
 ./make.sh user_click

make radiusReform:
 ./clean.sh
 ./make tool
 ./clean.sh
 ./make radiusReform