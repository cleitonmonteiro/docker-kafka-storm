## docker-kafka-storm
Dockerized Big Data Stream Processing Pipeline for Analyzing Data with [Apache Kafka](http://kafka.apache.org/), [Apache Storm](http://storm.apache.org/). 

The ```build.sh``` executes the Dockerfiles stored under kafka/example-kafka-producer/ for building the ***kafka-producer*** and under storm/mobile-geolocation for building the ***storm-topology*** image as well as the images in specified within the ```docker-compose.yml``` file.
```shell
➜  docker-kafka-storm git:(master) ./build.sh
Usage: ./build.sh {initial|kafka-producer|storm-topology}
➜  docker-kafka-storm git:(master) ./build.sh initial
```

Once the images are built, you can start the multi-container application stack by running ```compose.sh``` with one of two options. To run in foreground choose ```start-foreground```. To run in detached mode choose ```start-background```. For debugging and learning purposes it is good to run in the foreground.
```shell
➜  docker-kafka-storm git:(master) ./compose.sh
Usage: ./compose.sh {start-foreground|start-background|stop}
➜  docker-kafka-storm git:(master) ./compose.sh start-foreground
Creating docker-kafka-storm_zookeeper_1
Creating docker-kafka-storm_nimbus_1
Creating docker-kafka-storm_stormui_1
Creating docker-kafka-storm_supervisor_1
Creating docker-kafka-storm_kafka_1
...
```

Once the multi-container application stack is up and running you first need to create a ***topic*** to which the messages are published to. Additionally you will have to pass the ***replication-factor*** and the number of ***partions*** as arguments, e.g. ```./create-kafka-topic.sh <replication-factor> <partition> <topic>```.
```shell
➜  docker-kafka-storm git:(master) ./create-kafka-topic.sh 1 1 mobile-location
Created topic "mobile-location".
```

Then, the ```MainTopology``` is submitted to the "cluster" by calling ```submit-storm-topology.sh```. Additionally you will have to pass the ***com.example.MainClass***, ***topology-name*** and ***topic*** as arguments, e.g. ```./submit-storm-topology.sh <com.example.MainClass> <topology-name> <topic>```. You can go and check the Storm UI http://DOCKER_HOST_IP:8080 and see the deployed topology. (Note: Native Linux OS users can see the Storm UI under http://localhost:8080).

***NOTE***: **Sometimes it takes a bit for the Storm UI to successfully load.**
```shell
➜  docker-kafka-storm git:(master) ./submit-storm-topology.sh io.github.cleitonmonteiro.MainTopology location-top mobile-location
```

You can check the computation output by executing ```show-storm-output.sh``` which shows the log output from within the supervisor container on ```/var/log/storm/worker-6702.log```.
```shell
➜  docker-kafka-storm git:(master) ./show-storm-output.sh
...
2015-11-25 21:46:27 c.b.s.b.RankerBolt [INFO] [the=1569, and=770, seven=770, snow=417, white=417, dwarfs=417, cow=385, doctor=385, over=385, keeps=385, away=385, apple=385, an=385, day=385, moon=384, a=384, jumped=384, score=353, four=353, years=353, ago=353, nature=350, with=350, at=350, i=350, two=350, am=350]
...
```

You can kill the Storm topology by executing ```kill-storm-topology.sh``` and passing the ***topology-name*** as an argument, e.g. ```kill-storm-topology.sh <topology-name>```.
```shell
➜  docker-kafka-storm git:(master) ./kill-storm-topology.sh mobile-location
...
1347 [main] INFO  backtype.storm.thrift - Connecting to Nimbus at 172.17.0.3:6627
1499 [main] INFO  backtype.storm.command.kill-topology - Killed topology: mobile-location
```

To stop the running multi-container application stack execute ```compose.sh``` again, but this time with ```stop``` as the option.
```shell
➜  docker-kafka-storm git:(master) ./compose.sh stop
Stopping docker-kafka-storm_kafka_1 ... done
Stopping docker-kafka-storm_supervisor_1 ... done
Stopping docker-kafka-storm_stormui_1 ... done
Stopping docker-kafka-storm_nimbus_1 ... done
Stopping docker-kafka-storm_zookeeper_1 ... done
```

## Credits
Credits belong to the work of [wurstmeister](https://github.com/wurstmeister) and [ches](https://github.com/ches) for putting [Apache Storm](https://github.com/wurstmeister/storm-docker) and [Apache Kafka](https://github.com/ches/kafka) in a Docker container. Check their repositories on GitHub.
