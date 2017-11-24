#!/bin/bash
CWD=`echo $(dirname $(readlink -f $0))`
cd `dirname $(readlink -f $0)`

source $CWD/s2-init.sh

# command:
exec $JAVA_HOME/bin/java -cp $CP $AGENT $OPT com.syspulse.s2.Loader $@
