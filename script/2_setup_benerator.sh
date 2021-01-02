#!/bin/bash

ARTIFACT_ID=rapiddweller-benerator-ce
ARTIFACT_VERSION=$(mvn -q \
    -Dexec.executable=echo \
    -Dexec.args='${project.version}' \
    --non-recursive \
    exec:exec)
BENERATOR_NAME=$ARTIFACT_ID-$ARTIFACT_VERSION

echo "###################################################################"
echo "install rapiddweller-benerator-ce as local executeable application"
echo "###################################################################"

cd "$PWD" || exit
mvn versions:set -DnewVersion="$ARTIFACT_VERSION"
mvn clean install assembly:single -Dmaven.test.skip=true -Dmaven.javadoc.skip=true
rm -rd ~/"$BENERATOR_NAME"
tar -xvzf target/"$BENERATOR_NAME"-dist.tar.gz -C ~
export BENERATOR_HOME=~/"$BENERATOR_NAME"
export PATH=$PATH:~/"$BENERATOR_NAME"/bin
chmod -R 777 ~/"$BENERATOR_NAME"/bin/

echo "###################################################################"
echo "following version has been installed ..."
echo "###################################################################"

benerator --version
