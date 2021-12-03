package io.github.cleitonmonteiro;

import io.github.cleitonmonteiro.bolts.CounterBolt;
import io.github.cleitonmonteiro.bolts.LocationBolt;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MainTopology {
    private static final Logger LOG = LoggerFactory.getLogger(MainTopology.class);
    private static final String KAFKA_SPOUT_ID = "KAFKA_SPOUT_ID";
    private static final String LOCATION_BOLT_ID = "LOCATION_BOLT_ID";
    private static final String NOTIFIER_BOLT_ID = "NOTIFIER_BOLT_ID";
    private static final String COUNTER_BOLT_ID = "counter-bolt";
    private BrokerHosts brokerHosts;

    public MainTopology(String ZK_HOST, String ZK_PORT) {
        brokerHosts = new ZkHosts(ZK_HOST + ":" + ZK_PORT);
    }

    /**
     * Using Kafka as spout
     *
     * @return      StormTopology Object
     */
    public StormTopology buildTopology(String TOPIC) {

        SpoutConfig kafkaConf = new SpoutConfig(brokerHosts, TOPIC, "", "storm");
        kafkaConf.scheme = new SchemeAsMultiScheme(new StringScheme());

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(KAFKA_SPOUT_ID, new KafkaSpout(kafkaConf));
        builder.setBolt(LOCATION_BOLT_ID, new LocationBolt(), 4).shuffleGrouping(KAFKA_SPOUT_ID);
        builder.setBolt(NOTIFIER_BOLT_ID, new CounterBolt(), 4).fieldsGrouping(LOCATION_BOLT_ID, new Fields("notification"));
        // builder.setBolt(RANKER_BOLT_ID, new RankerBolt()).globalGrouping(COUNTER_BOLT_ID);

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

            conf.setDebug(false);
            conf.setNumWorkers(2);
            conf.setMaxTaskParallelism(5);
            // conf.put(Config.NIMBUS_HOST, NIMBUS_HOST);
            conf.put(Config.NIMBUS_THRIFT_PORT, NIMBUS_THRIFT_PORT);
            conf.put(Config.STORM_ZOOKEEPER_PORT, ZK_PORT);
            conf.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(ZK_HOST));

            MainTopology wordCountTopology = new MainTopology(ZK_HOST, String.valueOf(ZK_PORT));
            StormSubmitter.submitTopology(TOPOLOGY_NAME, conf, wordCountTopology.buildTopology(TOPIC));
        } else {
            LOG.info("Please pass all the args to topology");
        }
    }
}
