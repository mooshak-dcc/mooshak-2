#!/bin/bash

PLAYER_PATH=$1
LANGUAGE=$2

PLAYER_ID="$(dirname -- "$PLAYER_PATH" | sed 's!.*/!!')"

/usr/bin/js52 -f wrappers/$LANGUAGE/PlayerWrapper.js -- $PLAYER_PATH $PLAYER_ID

# $home/js-asura-executor.sh $file es6
# /usr/bin/js $programFolder/wrappers/es6/PlayerWrapper.js $file w
