#!/bin/bash

mkbuildsub()
{

if [ ! -d $1/class ] ; then
echo mkdir $1/class
fi
if [ ! -d $1/touch ] ; then
echo mkdir $1/touch
fi

echo pushd $1 >/dev/null

pushd $1/src >/dev/null
find . -name "*.java" -exec echo mkjava $1 {} \;
find . -name "*.scala" -exec echo mkscala $1 {} \;
popd >/dev/null

echo popd >/dev/null

} #mkbuildsub

{

cat <<EOS
S=0
mkjava()
{
  if [ \$1/src/\$2 -nt \$1/touch/\$2.touch ] ; then
    echo javac -classpath ./class \$1/\$2    
    javac -classpath ./class -sourcepath \$1/src -d \$1/class \$1/src/\$2
    if [ \$? -ne 0 ] ; then
    S=1
    else
    mkdir -p \`dirname \$1/touch/\$2.touch\`
    touch \$1/touch/\$2.touch
    fi
  fi
}
mkscala()
{
  if [ \$1/src/\$2 -nt \$1/touch/\$2.touch ] ; then
    echo scalac -classpath ./class \$1/\$2    
    fsc -classpath ./class -sourcepath \$1/src -d \$1/class \$1/src/\$2
    if [ \$? -ne 0 ] ; then
    S=1
    else
    mkdir -p \`dirname \$1/touch/\$2.touch\`
    touch \$1/touch/\$2.touch
    fi
  fi
}
EOS

mkbuildsub .
mkbuildsub ./test

cat <<EOS
if [ \$S -ne 0 ] ; then
exit 1
fi

# execute test
java -classpath ./test/class:./class:$SCALA_HOME/lib/* Test
#scala -cp ./test/class:./class Test
if [ \$? -ne 0 ] ; then
exit 1
fi

echo "compile & test Success"

EOS

} > build.tmp

. build.tmp 2>&1

rm build.tmp


