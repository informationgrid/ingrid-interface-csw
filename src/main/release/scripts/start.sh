###
# **************************************************-
# ingrid-interface-csw
# ==================================================
# Copyright (C) 2014 - 2023 wemove digital solutions GmbH
# ==================================================
# Licensed under the EUPL, Version 1.1 or – as soon they will be
# approved by the European Commission - subsequent versions of the
# EUPL (the "Licence");
# 
# You may not use this work except in compliance with the Licence.
# You may obtain a copy of the Licence at:
# 
# http://ec.europa.eu/idabc/eupl5
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the Licence is distributed on an "AS IS" basis,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the Licence for the specific language governing permissions and
# limitations under the Licence.
# **************************************************#
###
#!/bin/sh
#
##
# Environment Variables
#
#   INGRID_JAVA_HOME Overrides JAVA_HOME.
#
#   INGRID_OPTS      addtional java runtime options
#
#	INGRID_USER 	 starting user, default ist "ingrid"
#

THIS="$0"

# some directories
THIS_DIR=`dirname "$THIS"`
INGRID_HOME=`cd "$THIS_DIR" ; pwd`
PID=$INGRID_HOME/ingrid.pid

# include default options, i.e. debug, jmx and jvm options
if [ -f $INGRID_HOME/env.user.sh ]; then
  eval `sh $INGRID_HOME/env.user.sh`
elif [ -f $INGRID_HOME/env.sh ]; then
  eval `sh $INGRID_HOME/env.sh`
fi

# functions
stopIplug()
{
  echo "Try stopping ingrid component ($INGRID_HOME)..."
  if [ -f $PID ]; then
		procid=`cat $PID`
		idcount=`ps -p $procid | wc -l`
		if [ $idcount -eq 2 ]; then
			echo stopping $command, wait 3 sec to exit.
			kill `cat $PID`
			sleep 3
			idcount=`ps -p $procid | wc -l`
			if [ $idcount -eq 1 ]; then
				echo "process ($procid) has been terminated."
				unlink $PID
				exit 0
			else
				COUNTER=1
				SECS=0
				while [  $COUNTER -lt 10 ]; 
				do
					COUNTER=$(($COUNTER + 1))
					echo "process is still running. wait 1 more sec."
					sleep 1
					idcount=`ps -p $procid | wc -l`
					if [ $idcount -eq 1 ]; then
						SECS=$(($COUNTER + $SECS))
					    echo "process ($procid) has been terminated after $SECS sec."
					    unlink $PID
					    exit 0
					fi					
				done
				echo "process is still running. force kill -9."
				kill -9 `cat $PID`
				exit 0
			fi 
		else
			echo "process is not running. Exit."
			unlink $PID 
		fi
	else
		echo "process is not running. Exit."
	fi
}

stopNoExitIplug()
{
  echo "Try stopping jetty ($INGRID_HOME)..."
  if [ -f $PID ]; then
		procid=`cat $PID`
		idcount=`ps -p $procid | wc -l`
		if [ $idcount -eq 2 ]; then
			echo stopping $command, wait 3 sec to exit.
			kill `cat $PID`
			sleep 3
			idcount=`ps -p $procid | wc -l`
			if [ $idcount -eq 1 ]; then
				echo "process ($procid) has been terminated."
				unlink $PID
			else
				COUNTER=1
				SECS=0
				while [  $COUNTER -lt 10 ]; 
				do
					COUNTER=$(($COUNTER + 1))
					echo "process is still running. wait 1 more sec."
					sleep 1
					idcount=`ps -p $procid | wc -l`
					if [ $idcount -eq 1 ]; then
						SECS=$(($COUNTER + $SECS))
					    echo "process ($procid) has been terminated after $SECS sec."
					    unlink $PID
					fi					
				done
				echo "process is still running. force kill -9."
				kill -9 `cat $PID`
			fi
		else
			echo "process is not running. Exit."			
		fi
    else
      echo "process is not running. Exit."
    fi
}

prepareJavaStatement()
{
    # some Java parameters
    if [ "$INGRID_JAVA_HOME" != "" ]; then
      #echo "run java in $INGRID_JAVA_HOME"
      JAVA_HOME=$INGRID_JAVA_HOME
    fi

    if [ "$JAVA_HOME" = "" ]; then
      echo "Error: JAVA_HOME is not set."
      exit 1
    fi

    JAVA=$JAVA_HOME/bin/java

    # so that filenames w/ spaces are handled correctly in loops below
    IFS=
    # add libs to CLASSPATH
    CLASSPATH=${CLASSPATH}:${INGRID_CONF_DIR:=$INGRID_HOME/conf}
    CLASSPATH_POSTFIX=""
    for f in $INGRID_HOME/lib/*.jar; do
      # make sure ingrid libraries appear first in classpath
      # this enables overwriting of 3-rd party library code
      case "$f" in
         *lib/ingrid*) CLASSPATH=${CLASSPATH}:$f;;
         *) CLASSPATH_POSTFIX=${CLASSPATH_POSTFIX}:$f;;
      esac
    done
    CLASSPATH=${CLASSPATH}:$CLASSPATH_POSTFIX;
    # restore ordinary behaviour
    unset IFS

    # cygwin path translation
    if expr `uname` : 'CYGWIN*' > /dev/null; then
      CLASSPATH=`cygpath -p -w "$CLASSPATH"`
      INGRID_HOME=`cygpath -p -w "$INGRID_HOME"`
    fi

    # run it
    export CLASSPATH="$CLASSPATH"
    INGRID_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.xml/com.sun.org.apache.xerces.internal.dom=ALL-UNNAMED --add-opens java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED --add-opens java.xml/com.sun.org.apache.xerces.internal.util=ALL-UNNAMED -Dpolyglot.engine.WarnInterpreterOnly=false -Dingrid_home=$INGRID_HOME $INGRID_OPTS"
}


startIplug()
{
  echo "Try starting jetty ($INGRID_HOME)..."
  if [ -f $PID ]; then
      procid=`cat $PID`
      idcount=`ps -p $procid | wc -l`
      if [ $idcount -eq 2 ]; then
        echo plug running as process `cat $PID`.  Stop it first.
        exit 1
      fi
  fi
  
  prepareJavaStatement
  
  CLASS=de.ingrid.interfaces.csw.admin.JettyStarter
  
  if [ "$RUN_DIRECTLY" ]; then
    exec "$JAVA" $INGRID_OPTS $CLASS
  else
    exec nohup "$JAVA" $INGRID_OPTS $CLASS > console.log &
  fi
  
  echo "jetty ($INGRID_HOME) started."
  echo $! > $PID
}

# make sure the current user has the privilege to execute that script
if [ "$INGRID_USER" = "" ]; then
  INGRID_USER="ingrid"
fi

STARTING_USER=`whoami`
if [ "$STARTING_USER" != "$INGRID_USER" ]; then
  echo "You must be user '$INGRID_USER' to start that script! Set INGRID_USER in environment to overwrite this."
  exit 1
fi 

case "$1" in
  start)
    startIplug
    ;;
  debug)
    INGRID_OPTS="$INGRID_OPTS -agentlib:jdwp=transport=dt_socket,address=7090,server=y,suspend=n"
    startIplug
    ;;
  stop)
    stopIplug
    ;;
  restart)
    stopNoExitIplug
    echo "sleep 3 sec ..."
    sleep 3
    startIplug
    ;;
  status)
    if [ -f $PID ]; then
      procid=`cat $PID`
      idcount=`ps -p $procid | wc -l`
      if [ $idcount -eq 2 ]; then
        echo "process ($procid) is running."
      else
        echo "process is not running. Exit."
      fi
    else
      echo "process is not running. Exit."
    fi
    ;;
  resetPassword)
    prepareJavaStatement
    CLASS=de.ingrid.interfaces.csw.admin.command.AdminManager
    exec "$JAVA" $INGRID_OPTS $CLASS reset_password $2
    echo "Please restart the iPlug to read updated configuration."
    ;;
  calcPassword)
    prepareJavaStatement
    CLASS=de.ingrid.interfaces.csw.admin.command.AdminManager
    exec "$JAVA" $INGRID_OPTS $CLASS calc_password $2
    echo "Please restart the iPlug to read updated configuration."
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status|resetPassword <newPassword>|calcPassword <password>}"
    exit 1
    ;;
esac
