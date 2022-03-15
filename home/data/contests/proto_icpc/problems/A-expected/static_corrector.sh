#!/bin/sh


home=${1:-}
program=${2:-}
problem=${3:-}
solution=${4:-}
environment=${5:-}


res=0
mark=128

/bin/sh $problem/line_counter.sh $program 7 6
status=$?
if [ $status -gt $res ]
then
  if [ $status -gt 127 ]
  then
    if [ $status -gt $mark ]
    then
      mark=$status
    fi
  else
    res=$status
  fi
fi

[ $res -eq 0 -a $mark -eq 128 ] && exit 0
[ $res -eq 0 ] && exit $mark
exit $res
