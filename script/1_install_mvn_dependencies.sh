#!/bin/bash

programs=(
  "git"
  "mvn"
  "java"
  "tar"
)

echo "###################################################################"
echo "necessary programs check ..."
for program in "${programs[@]}"; do
  echo "###################################################################"
  echo "check $program ..."
  echo "###################################################################"
  if ! [ -x "$(command -v "$program")" ]; then
  echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo "Error: $program is not installed." >&2
  echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  exit 1
  else
    echo "###################################################################"
    echo "$program seems to be installed ..."
    echo "###################################################################"
fi
done
echo "###################################################################"

modules=(
  "rd-lib-common"
  "rd-lib-format"
  "rd-lib-script"
  "rd-lib-contiperf"
  "rd-lib-jdbacl"
  "rapiddweller-benerator-ce"
)

echo "###################################################################"
echo "make sure you have following projects checked out in one directory"
for module in "${modules[@]}"; do
  echo "you need $module ..."
done
echo "###################################################################"


cd "$PWD" || exit
for module in "${modules[@]}"; do
  DIR=../"$module"
  if [ -d "$DIR" ]; then
    ### Take action if $DIR exists ###
    echo "###################################################################"
    echo "###################################################################"
    echo "module $module already checked out"
    echo "###################################################################"
    echo "###################################################################"
  else
    echo "###################################################################"
    echo "###################################################################"
    echo "checking out module $module into $DIR"
    echo "###################################################################"
    echo "###################################################################"
    ###  Control will jump here if $DIR does NOT exists ###
    git clone --branch development https://gitlab.com/rapiddweller/benerator/"$module".git "$DIR"
  fi
  echo "###################################################################"
  echo "###################################################################"
  echo "install $module into maven local repository"
  echo "###################################################################"
  echo "###################################################################"
  cd "$DIR" && mvn clean install -DskipTests
done
