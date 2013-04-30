#!/bin/sh
#
##
# Environment Variables
#
#   INGRID_JAVA_HOME Overrides JAVA_HOME.
#
#   INGRID_HEAPSIZE  heap to use in mb, if not setted we use 1000.
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

# include a debug script, if available, i.e. to specify debug port, etc.
# caution: the debug script must echo the actual command to be able to work in the current environment
if [ -f $INGRID_HOME/debug.sh ]; then
  eval `sh $INGRID_HOME/debug.sh`
fi

# include a jmx script, if available, i.e. to specify jmx port, etc.
# caution: the jmx script must echo the actual command to be able to work in the current environment
if [ -f $INGRID_HOME/jmx.sh ]; then
  eval `sh $INGRID_HOME/jmx.sh`
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
  JAVA_HEAP_MAX=-Xmx256m
  
  # check envvars which might override default args
  if [ "$INGRID_HEAPSIZE" != "" ]; then
    JAVA_HEAP_MAX="-Xmx""$INGRID_HEAPSIZE""m"
    echo "run with heapsize $JAVA_HEAP_MAX"
  fi

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
    INGRID_HOME=`cygpath -w "$INGRID_HOME"`
  fi

  # run it
  export CLASSPATH="$CLASSPATH"
  INGRID_OPTS="$INGRID_OPTS -Dingrid_home=$INGRID_HOME -Dfile.encoding=UTF8"
  CLASS=de.ingrid.interfaces.csw.admin.JettyStarter
	
  exec nohup "$JAVA" $JAVA_HEAP_MAX $INGRID_OPTS $CLASS > console.log & 
  
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
  *)
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
    ;;
esac
