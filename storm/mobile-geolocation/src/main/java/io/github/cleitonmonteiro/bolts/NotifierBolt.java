package io.github.cleitonmonteiro.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NotifierBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(io.github.cleitonmonteiro.bolts.NotifierBolt.class);
    private OutputCollector collector;
    private MongoDatabase mobileDatabase;
    private CodecRegistry pojoCodecRegistry;
    private String mongoHost;

    public NotifierBolt(String mongoHost) {
        this.mongoHost = mongoHost;
    }

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try {
            MongoClientURI mongoClientURI = new MongoClientURI(String.format("mongodb://root:rootpass@%s:27017/mobile?authSource=admin", mongoHost));
            MongoClient mongoClient = new MongoClient(mongoClientURI);
            mobileDatabase = mongoClient.getDatabase("mobile");
            PojoCodecProvider codecProvider = PojoCodecProvider.builder()
                    .automatic(true)
                    .build();
            pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromProviders(codecProvider));
        } catch (Exception e) {
            LOG.info("Cannot connect to mongodb!" + e.getMessage());
        }
    }

    public void execute(Tuple tuple) {
        try {
            Gson gson = new Gson();
            String notification = tuple.getStringByField("notification");
            LOG.info("NotifierBolt: " + notification);
            collector.ack(tuple);
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
