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

pushd ./class/scala/tools/nsc/interpreter >/dev/null
{
find . -exec echo 'echo create scala/tools/interpretex/{}' \; echo perl -p -e '/scala\\/tools\\/nsc\\/interpreter/scala\\/tools\\/nsc\\/interpretex/g' \< ./class/scala/tools/nsc/interpreter/{} \> ./class/scala/tools/nsc/interpretex/{} \;
} > build1.tmp
popd >/dev/null

#. build1.tmp

fi
