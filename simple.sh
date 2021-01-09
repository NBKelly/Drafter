#!/bin/bash

SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
javac -classpath $SCRIPTPATH "$SCRIPTPATH/easy/EasyDrafter.java"

if [ $? -ne 0 ]; then
    echo "Could not compile easydrafter"
    exit 1
fi

output=$(java -classpath $SCRIPTPATH easy.EasyDrafter $@)
echo "$SCRIPTPATH/drafter.sh $output"
#echo $output

$SCRIPTPATH/drafter.sh $output

