#!/bin/bash

pid=

function help
{
    echo "

    Stop microservice. Search run.pid file
        
        Options include:

            -h | --help       Display options.            
    "
}

while [ "$1" != "" ]; do
    case $1 in
        -h | --help)
            help
            exit
            ;;
        *)
    esac
    shift
done

pid=`cat run.pid | awk '{print $1}'`
if [ "$pid" = "" ]; then
    echo "PID not found"
    exit
fi

echo "PID: $pid"
echo "Killing process: $PID"
kill -9 $pid