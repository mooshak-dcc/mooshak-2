#!/bin/bash

timeout=${1:-5}

#active wait
#end=$((SECONDS+$timeout))

#while [ $SECONDS -lt $end ]; do
#	:
#done

#inactive wait
sleep $timeout

hash=`echo 'Olá mundo!' | md5sum | awk '{print $1}'`

echo 'LDAP Sign in -- SUCCESS'
echo 'hash:' $hash


