#!/bin/sh

l=`cat $1 | sed '/^\s*$/d' | wc -l`

if [ "$l" -ge $2 ]; then # threshold for passing
  echo "Reduce lines"
  exit 2
fi

if [ "$l" -ge $3 ]; then # threshold for 50% grade
  echo "Reduce one more line"
  exit $((256-50))
fi

exit 0
