#!/bin/bash
CWD=`echo $(dirname $(readlink -f $0))`
cd `dirname $(readlink -f $0)`

# ATTENTION: must be created by sbt
echo "INF: resolving libraries..."
CP=`find lib_managed/ -name "*.jar" | paste -sd ":" -`

S2_CORE=`find -L s2-core/target/scala-2.11 -name "s2*.jar" | paste -sd ":" -`
S2_SEARCH=`find -L s2-search/target/scala-2.11 -name "s2*.jar" | paste -sd ":" -`
S2_SHELL=`find -L s2-shell/target/scala-2.11 -name "s2*.jar" | paste -sd ":" -`
S2_SIMILARITY=`find -L s2-similarity/target/scala-2.11 -name "s2*.jar" | paste -sd ":" -`
S2_PLUGINS=`find -L ./plugins/ -name "*.jar" | paste -sd ":" -`

CP=`pwd`/conf/:$S2_CORE:$S2_SEARCH:$S2_SHELL:$S2_SIMILARITY:$S2_PLUGINS:$CP

echo "CP=$CP"| sed "s/\:/\n/g"
echo "OPT=$OPT"

