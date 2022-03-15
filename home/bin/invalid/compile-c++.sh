#!/bin/sh

ROOT="/home/v2"

setcommand ()
{
    COMMAND="g++ -o $OUT $ROOT/compile/invalid.o -x c++ $INP -Wall -ansi -pedantic"
    return 0
}

case $# in
    1)
        if [ $1 = "--command" ]
            then
            INP="<input file>"
            OUT="<output file>"
            setcommand
            echo $COMMAND
            exit 0
        else
            echo "Invalid option ($1)"
            exit 2
        fi ;;
    2)
        INP="$1"
        OUT="$2"
        setcommand
        if [ $1 = $2 ]
            then
            echo "Destination file is the source file ($1)"
            exit 2
        fi
        $COMMAND 2>&1
        if [ $? -eq 0 ]
            then
            exit 0
        else
            exit 1
        fi
        ;;
    *)
        echo "Invalid number of parameters ($#)"
        exit 2 ;;
esac
