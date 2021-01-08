#!/bin/bash

#stackoverflow says this is the best way to get the path...
SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

#-classpath lets us keep the directories we supply, while making the resources load relative
# to the java class.
javac -classpath "$SCRIPTPATH" com/nbkelly/working/MakeHelper.java
java -classpath "$SCRIPTPATH" com.nbkelly.working.MakeHelper "$@"
