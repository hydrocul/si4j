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

mkdir ./class/scala/tools/nsc/interpretex
mkdir ./class/scala/tools/nsc/tmp
{
pushd ./class/scala/tools/nsc/interpreter >/dev/null
find . -exec echo "echo 'create class/scala/tools/interpretex/{}'" \; \
  -exec echo "perl -p -e 's/scala\\/tools\\/nsc\\/interpreter/scala\\/tools\\/nsc\\/interpretex/g' < './class/scala/tools/nsc/interpreter/{}' > './class/scala/tools/nsc/tmp/{}'" \; \
  -exec echo "perl -p -e 's/scala\\\$tools\\\$nsc\\\$interpreter/scala\\\$tools\\\$nsc\\\$interpretex/g' < './class/scala/tools/nsc/tmp/{}' > './class/scala/tools/nsc/interpretex/{}'" \;
popd >/dev/null
} > build1.tmp
. build1.tmp
rm -r ./class/scala/tools/nsc/tmp

fi
