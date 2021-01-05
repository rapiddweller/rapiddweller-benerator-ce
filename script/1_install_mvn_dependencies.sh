#!/bin/bash

###################################################################
# check java target version
###################################################################

TARGET_VERSION=11

###################################################################
# check if necessary programs are installed
###################################################################

programs=(
  "git"
  "mvn"
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

###################################################################
# check if Environment variable is set  $SHELL
###################################################################

if [ -z "$SHELL" ]; then
  echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo "Error: Environment variable SHELL is not installed." >&2
  echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  exit 1
else
  echo "###################################################################"
  echo "Environment variable is set SHELL is set to $SHELL"
  echo "###################################################################"
fi

###################################################################
# check if java version is configures and installed correctly
# and if version fits the target version
###################################################################

if type -p java; then
  echo found java executable in PATH
  _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
  _java="$JAVA_HOME/bin/java"
else
  echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo "Error: JAVA VERSION is too less. install at least version $TARGET_VERSION"
  echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  exit 1
fi

if [[ "$_java" ]]; then
  version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
  echo version "$version"
  if [[ "$version" > $TARGET_VERSION ]]; then
    echo "###################################################################"
    echo "Java $TARGET_VERSION seems to be installed and and configured correctly ..."
    echo "###################################################################"
  else
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    echo "Error: JAVA VERSION is too less. install at least version $TARGET_VERSION" >&2
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    exit 1
  fi
fi

###################################################################
# list necessary benerator modules
###################################################################

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

###################################################################
# check missing projects out from gitlab repository
###################################################################

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
    git clone --branch development https://gitlab.com/rapiddweller/benerator/"$module".git "$DIR"
  fi
  echo "###################################################################"
  echo "###################################################################"
  echo "install $module into maven local repository"
  echo "###################################################################"
  echo "###################################################################"
  cd "$DIR" && mvn clean install -DskipTests
done
