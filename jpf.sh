#!/bin/bash
#
# unix shell script to run jpf
#

JPF_HOME=`dirname "$0"`/..

if test -z "$JVM_FLAGS"; then
  JVM_FLAGS="-Xmx10g -ea"
fi

java $JVM_FLAGS -jar "$JPF_HOME/build/RunJPF.jar" "$@"
