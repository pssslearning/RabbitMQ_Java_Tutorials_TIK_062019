#!/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
figlet -f small Tutorial 4
figlet -f small ReceiveLogsDirect
figlet -f small - Error -
java -cp ../../target/tik062019-1.0.0-jar-with-dependencies.jar tutorial4/ReceiveLogsDirectError