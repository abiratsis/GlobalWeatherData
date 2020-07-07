#!/bin/sh
java=java
if test -n "$JAVA_HOME"; then
    java="$JAVA_HOME/bin/java"
fi
exec "$java" -cp gweather.jar com.abiratsis.gweather.Main "$@"
exit 1