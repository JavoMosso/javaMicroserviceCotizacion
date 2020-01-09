#!/bin/bash

file=
profile=default
memory=128m

function help
{
    echo "

    Run microservice. Find the first .jar file and run
        
        Options include:

            -p | --profile    Set profile. Default value is 'default'.
            -m | --memory     Set a -Xmx Java Option. Default value is '128m'.
            -f | --file       Set .jar file to run.
            -h | --help       Display options.            
    "
}

while [ "$1" != "" ]; do
    case $1 in
        -p | --profile )
            profile=$2
            ;;
        -m | --memory )
            memory=$2
            ;;
        -f | --file)
            file=$2
            ;;
        -h | --help)
            help
            exit
            ;;
        *)
    esac
    shift
done

if [ "$file" = "" ]; then
    file=`ls | grep '\.jar$' | awk '{print $1;exit}'`
    if [[ "" !=  "$file" ]]; then
      echo "File: $file"
    fi
fi
if [ "$file" = "" ]; then
    echo "File not found"
    exit
fi

echo "Profile: $profile"
echo "Memory: $memory"

echo "Running: $file"
java -server -Xmx$memory -Dspring.profiles.active=$profile -jar $file > configurador.out 2>&1 & echo $! > run.pid &

