#!/bin/bash
#
# unix shell script to run jpf
#

if test -z "$JVM_FLAGS"; then
  JVM_FLAGS="-Xmx10g -ea"
fi

java $JVM_FLAGS -jar "/opt/jpf-core/build/RunJPF.jar" "$@"
