#!/bin/bash
 
export JAVA_HOME=/usr/local/jdk-13.0.1
export JRE_HOME=/usr/local/jdk-13.0.1
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib:$CLASSPATH
export PATH=$JAVA_HOME/bin:$PATH
 
cd /home/pi
nohup java -jar NextCloudBot.jar > nextcloudbot.log 2>&1 &
