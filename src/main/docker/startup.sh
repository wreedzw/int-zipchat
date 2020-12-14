#!/bin/bash

#########################################################
# Docker startup script for Zipwhip microservices
#########################################################

# Set JVM memory limits
if [[ ${JAVA_XMX} ]]
then
  XMX=${JAVA_XMX}
else
  XMX=300m
fi

if [[ ${JAVA_XMS} ]]
then
  XMS=${JAVA_XMS}
else
  XMS=50m
fi

# Inject hosts entries
if [[ ${HOST_INJECT} ]]
then
  IFS=',' read -ra ADDR <<< "${HOST_INJECT}"
  for i in "${ADDR[@]}"; do
    echo "${i}" >> /etc/hosts
  done
fi

# perfino
if [[ ${PERFINO_ENABLE} = "true" ]]
then
  if [[ ${PERFINO_JAR} ]]
  then
    PJ=${PERFINO_JAR}
  else
    PJ=/perfino/perfino.jar
  fi

  if [[ ${PERFINO_SERVER} ]]
  then
    PS=${PERFINO_SERVER}
  else
    PS=perfino-internal.util
  fi

  # if [[ ${PERFINO_PORT} ]]
  # then
  #   PP=${PERFINO_PORT}
  # else
  #   PP=8847
  # fi

  if [[ ${PERFINO_NAME} ]]
  then
    PN=${PERFINO_NAME}
  else
    PN=${HOSTNAME}
  fi

  if [[ ${PERFINO_GROUP} ]]
  then
    PG=${PERFINO_GROUP}
  elif [[ ${KUBERNETES_NAMESPACE} ]]
  then
    PG=${KUBERNETES_NAMESPACE}
  elsegener
    PG=''
  fi

  PERFINO="-javaagent:${PJ}=server=${PS},name=${PN},group=${PG}"
  echo "Perfino enabled!"
else
  PERFINO=""
fi

# Startup!
echo Starting with XmS == ${XMS} and XmX == ${XMX}
exec /opt/jdk/bin/java ${CATALINA_OPTS} -Xmx${XMX} -Xms${XMS} ${PERFINO} -Djava.net.preferIPv4Stack=true -Djava.security.egd=file:/dev/./urandom -jar app.jar
