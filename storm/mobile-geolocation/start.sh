#!/bin/bash

case "$1" in
        submit)
            /usr/bin/storm jar target/mobile-geolocation-1.0-SNAPSHOT-jar-with-dependencies.jar ${MAINCLASS} ${TOPOLOGY_NAME} ${ZK_HOST} ${ZK_PORT} ${TOPIC} ${NIMBUS_HOST} ${NIMBUS_THRIFT_PORT} ${MONGO_HOST}
            ;;

        kill)
            /usr/bin/storm kill ${TOPOLOGY_NAME} -c nimbus.host=${NIMBUS_HOST} -c nimbus.thrift.port=${NIMBUS_THRIFT_PORT} -w 1
            ;;

        *)
            echo $"Usage: $0 {submit|kill}"
            exit 1

esac
