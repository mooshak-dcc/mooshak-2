#!/bin/bash

EXTRAS_FOLDER=$1
ARTIFACT_ID=$2
LANGUAGE=$3

#mkdir -p wrappers/$LANGUAGE/
#mkdir -p $ARTIFACT_ID/wrappers/$LANGUAGE/

ln -fsT $EXTRAS_FOLDER/wrappers/ wrappers
ln -fsT $EXTRAS_FOLDER/$ARTIFACT_ID/ $ARTIFACT_ID

#cp -Ra $EXTRAS_FOLDER/wrappers/$LANGUAGE/* wrappers/$LANGUAGE/
#cp -Ra $EXTRAS_FOLDER/$ARTIFACT_ID/wrappers/$LANGUAGE/* $ARTIFACT_ID/wrappers/$LANGUAGE/

# $home/workspace/Mooshak2.1Maven/js-asura-compiler.sh $gameExtras $gameArtifact es6

