#!/bin/sh

res=0
mark=255

/bin/sh line_counter.sh $program 5
status=$?
if [ $status -gt $res ]
then
  if [ $status -gt 127 ]
  then
    if [ $status -lt $mark ]
    then
      mark=$status
    fi
  else
    res=$status
  fi
fi

[ $res -eq 0 ] && exit $mark || exit $status
