#!/bin/bash

#stackoverflow says this is the best way to get the path...
SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

#-classpath lets us keep the directories we supply, while making the resources load relative
# to the java class.
javac -classpath "$SCRIPTPATH" "$SCRIPTPATH/"com/nbkelly/working/MakeHelper.java
if [ $? -ne 0 ]; then
    echo "Could not compile base class"
    exit
else
    java -classpath "$SCRIPTPATH" com.nbkelly.working.MakeHelper "$@"
fi
