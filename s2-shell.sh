#!/bin/bash
CWD=`echo $(dirname $(readlink -f $0))`
cd `dirname $(readlink -f $0)`

source $CWD/s2-init.sh

# command:
# load feeds/100.xml com.syspulse.s2.feed.dtv.FeedFileXML
exec $JAVA_HOME/bin/java -cp $CP $AGENT $OPT com.syspulse.s2.Shell $@
