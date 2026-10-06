#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ]; then
    if [ -f /usr/local/etc/mavenrc ]; then
        . /usr/local/etc/mavenrc
    fi
    if [ -f /etc/mavenrc ]; then
        . /etc/mavenrc
    fi
    if [ -f "$HOME/.mavenrc" ]; then
        . "$HOME/.mavenrc"
    fi
fi

if [ -z "$JAVA_HOME" ]; then
    JAVACMD="$(command -v java)" || { echo "Error: JAVA_HOME is not defined and java is not in the PATH." >&2; exit 1; }
else
    JAVACMD="$JAVA_HOME/bin/java"
    if [ ! -x "$JAVACMD" ]; then
        echo "Error: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
        exit 1
    fi
fi

find_maven_basedir() {
    if [ -z "$1" ]; then echo "Path not specified to find_maven_basedir" >&2; return 1; fi
    basedir="$1"
    wdir="$1"
    while [ "$wdir" != '/' ]; do
        if [ -d "$wdir"/.mvn ]; then basedir="$wdir"; break; fi
        if [ "$wdir" = '.' ]; then wdir="$(pwd)"; fi
        wdir="$(cd "$wdir/.." && pwd)"
    done
    printf '%s' "$(cd "$basedir" && pwd -P)"
}

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(find_maven_basedir "$(dirname "$0")")}"
export MAVEN_PROJECTBASEDIR

WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

if [ -r "$WRAPPER_PROPERTIES" ]; then
    WRAPPER_URL=$(grep -E '^wrapperUrl=' "$WRAPPER_PROPERTIES" | cut -d= -f2-)
fi
WRAPPER_URL="${WRAPPER_URL:-https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar}"

if [ ! -e "$WRAPPER_JAR" ]; then
    if [ "$MVNW_VERBOSE" = true ]; then
        echo "Downloading Maven Wrapper from $WRAPPER_URL"
    fi
    if command -v curl > /dev/null 2>&1; then
        curl -fsSL -o "$WRAPPER_JAR" "$WRAPPER_URL" || { echo "Error downloading Maven Wrapper." >&2; exit 1; }
    elif command -v wget > /dev/null 2>&1; then
        wget -q -O "$WRAPPER_JAR" "$WRAPPER_URL" || { echo "Error downloading Maven Wrapper." >&2; exit 1; }
    else
        echo "Error: Cannot download Maven Wrapper. Install curl or wget." >&2; exit 1
    fi
fi

if [ -f "$MAVEN_PROJECTBASEDIR/.mvn/jvm.config" ]; then
    JVM_CONFIG_MAVEN_PROPS=$(tr '\n' ' ' < "$MAVEN_PROJECTBASEDIR/.mvn/jvm.config")
fi

exec "$JAVACMD" \
    $JVM_CONFIG_MAVEN_PROPS \
    $MAVEN_OPTS \
    $MAVEN_DEBUG_OPTS \
    -classpath "$WRAPPER_JAR" \
    "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
    org.apache.maven.wrapper.MavenWrapperMain \
    $MAVEN_CONFIG "$@"
