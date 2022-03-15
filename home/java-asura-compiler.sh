#!/bin/bash

PROGRAM=$1
EXTRAS_FOLDER=$2
ARTIFACT_ID=$3
LANGUAGE=$4

#mkdir -p $PROGRAM_FOLDER/wrappers/$LANGUAGE/
#mkdir -p $PROGRAM_FOLDER/$ARTIFACT_ID/wrappers/$LANGUAGE/

#cp -R $EXTRAS_FOLDER/wrappers/$LANGUAGE/*.java $PROGRAM_FOLDER/wrappers/$LANGUAGE/
#cp -R $EXTRAS_FOLDER/$ARTIFACT_ID/wrappers/$LANGUAGE/*.java $PROGRAM_FOLDER/$ARTIFACT_ID/wrappers/$LANGUAGE/

ln -fsT $EXTRAS_FOLDER/wrappers/ wrappers
ln -fsT $EXTRAS_FOLDER/$ARTIFACT_ID/ $ARTIFACT_ID

mkdir output/

/usr/bin/javac -cp wrappers/$LANGUAGE/*:$ARTIFACT_ID/wrappers/$LANGUAGE/* \
	$PROGRAM \
	wrappers/$LANGUAGE/*.java \
	$ARTIFACT_ID/wrappers/$LANGUAGE/*.java \
	-d output/

# $home/java-asura-compiler.sh $file $gameExtras $gameArtifact java

