#!/bin/bash

javac com/nbkelly/working/MakeHelper.java

if [ $? -eq 0 ]; then
    if [ -z "$1" ]
    then
	java com.nbkelly.working.MakeHelper -d -n Advent01 -p build.nbkelly.working -l build/nbkelly/working/ -ap build.nbkelly.aux -ad build/nbkelly/aux/ --additional-imports imports.conf  --insert-code Inject.java
    else
	java com.nbkelly.working.MakeHelper -d "$1" -n Advent01 -p build.nbkelly.working -l build/nbkelly/working/ -ap build.nbkelly.aux -ad build/nbkelly/aux/ --additional-imports imports.conf  --insert-code Inject.java
    fi
fi
