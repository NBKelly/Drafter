#!/bin/bash

javac com/nbkelly/working/MakeHelper.java

if [ $? -eq 0 ]; then
    if [ -z "$1" ]
    then
	java com.nbkelly.working.MakeHelper -d --overwrite-main --overwrite-aux -n Advent01 -p build.nbkelly.working -l build/nbkelly/working/ -ap build.nbkelly.aux -ad build/nbkelly/aux/ --additional-imports imports.conf  --insert-block Inject.java --insert-params params.txt --insert-in-solution solve.txt --insert-commands command.txt --insert-in-post post.txt --silent-injections
    else
	java com.nbkelly.working.MakeHelper -d "$1" --overwrite-main --overwrite-aux -n Advent01 -p build.nbkelly.working -l build/nbkelly/working/ -ap build.nbkelly.aux -ad build/nbkelly/aux/ --additional-imports imports.conf  --insert-block Inject.java --insert-params params.txt --insert-in-solution solve.txt --insert-commands command.txt --insert-in-post post.txt --silent-injections
    fi

    echo
    echo "compiling..."

    javac build/nbkelly/working/Advent01.java
    if [ $? -eq 0 ]; then
	echo "> compile successful"
    else
	return 1
    fi

    echo "running..."
    echo
    
    java build.nbkelly.working.Advent01 -f out.txt

    if [ $? -ne 0 ]; then
	echo "> ran into an issue running the test project"
	return $?
    fi
fi
