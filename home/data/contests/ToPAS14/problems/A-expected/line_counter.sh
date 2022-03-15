#!/bin/sh

l=`cat $1 | sed '/^\s*$/d' | wc -l`

if [ "$l" -gt $2 ]; then
  exit 2
fi


