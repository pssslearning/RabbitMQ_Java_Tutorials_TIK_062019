#!/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
figlet -f small Addenda 1
figlet -f small Send With Props 
figlet -f small Durable Queue 
java -cp ../../target/tik062019-1.0.0-jar-with-dependencies.jar addenda1/SendWithPropsDurableQueue