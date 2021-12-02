#!/bin/bash

case "$1" in
        initial)
            docker build -t=kafka-producer kafka/example-kafka-producer
            docker build -t=storm-topology storm/mobile-geolocation

            docker-compose build
            ;;

        kafka-producer)
            docker build -t=kafka-producer kafka/example-kafka-producer
            ;;

        storm-topology)
            docker build -t=storm-topology storm/mobile-geolocation
            ;;

        *)
            echo $"Usage: $0 {initial|kafka-producer|storm-topology}"
            exit 1
esac
