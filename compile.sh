#!/bin/bash

if [ ! "$SCALA_HOME" ] ; then
echo "\$SCALA_HOME not defined"
exit 1
fi

if [ ! -d ./class ] ; then
mkdir ./class
echo unzip $SCALA_HOME/lib/scala-compiler.jar
pushd ./class >/dev/null
unzip $SCALA_HOME/lib/scala-compiler.jar
rm -r META-INF
rm compiler.properties
popd >/dev/null
fi
