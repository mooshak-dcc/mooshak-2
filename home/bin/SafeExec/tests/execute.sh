#!/bin/bash

for input in *.c
do
  output=`echo -e -n $input | sed s/\.c$//`
  echo -e -n "Running $input...\n"
  gcc -o $output $input -Wall -ansi -pedantic
  ../safeexec --exec $output > /dev/null
  rm -f $output
  echo -e -n "\n"
done
