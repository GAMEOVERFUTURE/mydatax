#!/bin/sh
# script directory
SCRIPT_PATH=$(readlink -f $0)
# bin directory
BIN_DIR=$(dirname $SCRIPT_PATH)
echo "BIN_DIR=$BIN_DIR"
# work directory
WORK_DIR=$(dirname $BIN_DIR)
echo "WORK_DIR=$WORK_DIR"
DEPLOY_DIR=$(dirname $WORK_DIR)
echo "DEPLOY_DIR=$DEPLOY_DIR"
# where is app lib path in location
LIB_DIR=${WORK_DIR}'/lib'
# app conf directory
JAVA_HOME='/app/smspay/tools/jdk1.8.0_181'
echo "Use JAVA_HOME=$JAVA_HOME"
JAVA_CMD=${JAVA_HOME}'/bin/java -jar'
echo "JAVA_CMD=$JAVA_CMD"
JAVA_OPTIONS="-Xms512m -Xmx512m"
CLASSPATH="${LIB_DIR}/sp_datax-*.jar"
echo "CLASSPATH=$CLASSPATH"
$JAVA_CMD $CLASSPATH $JAVA_OPTIONS >> $DEPLOY_DIR/catalina.out 2>&1 &

echo "$JAVA_CMD $CLASSPATH $JAVA_OPTIONS"

JAVA_PID=$!
echo "$JAVA_PID">$DEPLOY_DIR/server.pid
echo "$JAVA_PID is running."


