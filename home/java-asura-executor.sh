#!/bin/bash

PLAYER_NAME=$1
PLAYER_PATH=$2
ARTIFACT_ID=$3
LANGUAGE=$4

PLAYER_ID="$(dirname -- "$PLAYER_PATH" | sed 's!.*/!!')"

/usr/bin/java -cp output/:wrappers/$LANGUAGE/*:$ARTIFACT_ID/wrappers/$LANGUAGE/* \
	$PLAYER_NAME $PLAYER_NAME $PLAYER_ID

# $home/java-asura-executor.sh $name $file $gameArtifact java
