package io.github.cleitonmonteiro;

import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.Config;
import backtype.storm.tuple.Fields;
import io.github.cleitonmonteiro.bolts.LocationBolt;
import io.github.cleitonmonteiro.bolts.NotifierBolt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.kafka.*;

import java.util.Arrays;

public class MainTopology {
    private static final Logger LOG = LoggerFactory.getLogger(MainTopology.class);
    private static final String KAFKA_SPOUT_ID = "KAFKA_SPOUT_ID";
    private static final String LOCATION_BOLT_ID = "LOCATION_BOLT_ID";
    private static final String NOTIFIER_BOLT_ID = "NOTIFIER_BOLT_ID";
    private BrokerHosts brokerHosts;

    public MainTopology(String ZK_HOST, String ZK_PORT) {
        brokerHosts = new ZkHosts(ZK_HOST + ":" + ZK_PORT);
    }

    /**
     * Using Kafka as spout
     *
     * @return      StormTopology Object
     */
    public StormTopology buildTopology(String TOPIC, String mongoHost, String kafkaIP) {

        SpoutConfig kafkaConf = new SpoutConfig(brokerHosts, TOPIC, "", "storm");
        kafkaConf.scheme = new SchemeAsMultiScheme(new StringScheme());

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(KAFKA_SPOUT_ID, new KafkaSpout(kafkaConf));
        builder.setBolt(LOCATION_BOLT_ID, new LocationBolt(mongoHost), 4).shuffleGrouping(KAFKA_SPOUT_ID);
        builder.setBolt(NOTIFIER_BOLT_ID, new NotifierBolt(mongoHost , kafkaIP), 4).fieldsGrouping(LOCATION_BOLT_ID, new Fields("notification"));

        return builder.createTopology();
    }
    public static void main(String[] args) throws Exception {

        Config conf = new Config();
        String TOPOLOGY_NAME;

        if (args != null && args.length > 0) {
            TOPOLOGY_NAME = args[0];
            /**
             * Remote deployment as part of Docker Compose multi-application setup
             *
             * @TOPOLOGY_NAME:       Name of Storm topology
             * @ZK_HOST:             Host IP address of ZooKeeper
             * @ZK_PORT:             Port of ZooKeeper
             * @TOPIC:               Kafka Topic which this Storm topology is consuming from
             */
            LOG.info("Submitting topology " + TOPOLOGY_NAME + " to remote cluster.");
            String ZK_HOST = args[1];
            int ZK_PORT = Integer.parseInt(args[2]);
            String TOPIC = args[3];
            String NIMBUS_HOST = args[4];
            int NIMBUS_THRIFT_PORT = Integer.parseInt(args[5]);
            String mongoHost = args[6];

            String KAFKA_BROKER_HOST = args[7];
            String KAFKA_BROKER_PORT = args[8];

            conf.setDebug(false);
            conf.setNumWorkers(2);
            conf.setMaxTaskParallelism(5);
            conf.put(Config.NIMBUS_HOST, NIMBUS_HOST);
            conf.put(Config.NIMBUS_THRIFT_PORT, NIMBUS_THRIFT_PORT);
            conf.put(Config.STORM_ZOOKEEPER_PORT, ZK_PORT);
            conf.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(ZK_HOST));

            MainTopology wordCountTopology = new MainTopology(ZK_HOST, String.valueOf(ZK_PORT));
            StormSubmitter.submitTopology(TOPOLOGY_NAME, conf, wordCountTopology.buildTopology(TOPIC, mongoHost, KAFKA_BROKER_HOST + ":" + KAFKA_BROKER_PORT));
        } else {
            LOG.info("Please pass all the args to topology");
        }
    }
}
