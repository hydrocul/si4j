#!/bin/bash

pushd class >/dev/null
jar -cvf ../scala-compiler-si4j.jar *
popd >/dev/null
