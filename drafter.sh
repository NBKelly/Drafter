#!/bin/bash

SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
javac -classpath "$SCRIPTPATH" com/nbkelly/working/MakeHelper.java
java -classpath "$SCRIPTPATH" com.nbkelly.working.MakeHelper "$@"
