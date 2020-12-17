export ARTIFACT_ID=rapiddweller-benerator-ce
export ARTIFACT_VERSION=1.0.0+jdk-8

mvn clean install assembly:single -Dmaven.test.skip=true
rm -rd $ARTIFACT_ID-$ARTIFACT_VERSION
tar -xvzf target/$ARTIFACT_ID-$ARTIFACT_VERSION-dist.tar.gz
export BENERATOR_HOME=$PWD/$ARTIFACT_ID-$ARTIFACT_VERSION
export PATH=$PATH:$PWD/$ARTIFACT_ID-$ARTIFACT_VERSION/bin
chmod -R 777 $PWD/$ARTIFACT_ID-$ARTIFACT_VERSION/bin/
benerator --version
