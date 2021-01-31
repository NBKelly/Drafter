#!/bin/bash

javac com/nbkelly/working/MakeHelper.java

if [ $? -eq 0 ]; then
    if [ -z "$1" ]
    then
	java com.nbkelly.working.MakeHelper -d --overwrite-main --overwrite-aux -n Advent01 -p build.nbkelly.working -l build/nbkelly/working/ -ap build.nbkelly.aux -ad build/nbkelly/aux/ --additional-imports imports.conf  --insert-block Inject.java --insert-params params.txt --insert-in-solution solve.txt --insert-commands command.txt --insert-in-post post.txt --silent-injections
    else
	java com.nbkelly.working.MakeHelper -d "$1" --overwrite-main --overwrite-aux -n Advent01 -p build.nbkelly.working -l build/nbkelly/working/ -ap build.nbkelly.aux -ad build/nbkelly/aux/ --additional-imports imports.conf  --insert-block Inject.java --insert-params params.txt --insert-in-solution solve.txt --insert-commands command.txt --insert-in-post post.txt --silent-injections
    fi
fi
