#!/bin/bash

# clear an old docs
rm -r docs/javadocs

#generate docs
javadoc -link https://docs.oracle.com/en/java/javase/11/docs/api/ -d docs/javadocs com.nbkelly com.nbkelly.working
