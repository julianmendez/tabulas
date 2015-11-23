#!/bin/bash

cd `dirname $0`
date +%FT%T
echo `basename $0`

cd ../../..

mvn scala:run --quiet -DaddArgs="simple|src/test/resources/example.properties|target/example.properties"
mvn scala:run --quiet -DaddArgs="wikitext|src/test/resources/example.properties|target/example.text"
mvn scala:run --quiet -DaddArgs="html|src/test/resources/example.properties|target/example.html"

date +%FT%T


