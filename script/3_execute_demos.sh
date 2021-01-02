#!/bin/bash

export ARTIFACT_ID=rapiddweller-benerator-ce
export ARTIFACT_VERSION=1.1.0+jdk-11-SNAPSHOT
export BENERATOR_HOME=~/$ARTIFACT_ID-$ARTIFACT_VERSION
export PATH=$PATH:~/$ARTIFACT_ID-$ARTIFACT_VERSION/bin

echo "###################################################################"
echo "try some demo cases to make sure it works properly ..."
echo "###################################################################"

benerator --version

benerator $BENERATOR_HOME/demo/file/create_csv.ben.xml
benerator $BENERATOR_HOME/demo/file/create_dates.ben.xml
benerator $BENERATOR_HOME/demo/file/create_fixed_width.ben.xml
benerator $BENERATOR_HOME/demo/file/create_xls.ben.xml
benerator $BENERATOR_HOME/demo/file/create_xml.ben.xml
benerator $BENERATOR_HOME/demo/file/create_xml_by_script.ben.xml
benerator $BENERATOR_HOME/demo/file/csv_io.ben.xml
benerator $BENERATOR_HOME/demo/file/greetings_csv.ben.xml
benerator $BENERATOR_HOME/demo/file/import_fixed_width.ben.xml