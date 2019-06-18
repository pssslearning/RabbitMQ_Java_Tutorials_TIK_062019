#!/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
figlet -f small Tutorial 5
figlet -f small EmitLogTopic
java -cp ../../target/tik062019-1.0.0-jar-with-dependencies.jar tutorial5/EmitLogTopic