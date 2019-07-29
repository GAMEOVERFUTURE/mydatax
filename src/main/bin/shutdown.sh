#!/bin/sh
SCRIPT_PATH=$(readlink -f $0)
BIN_DIR=$(dirname $SCRIPT_PATH)
WORK_DIR=$(dirname $BIN_DIR)
DEPLOY_DIR=$(dirname $WORK_DIR)
pid=$(cat $DEPLOY_DIR/server.pid)
#kill $(cat $BIN_DIR/server.pid)
str=$(ps aux| grep -a $pid |grep -v 'grep')
if [ "$str" != "" ];then
  kill $pid
  echo "$pid is destoryed."
fi
exit 0
