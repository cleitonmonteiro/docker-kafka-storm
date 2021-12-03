package io.github.cleitonmonteiro.bolts;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.github.cleitonmonteiro.helper.GeolocationHelper;
import io.github.cleitonmonteiro.model.LocationKafkaMessage;
import io.github.cleitonmonteiro.model.LocationModel;
import io.github.cleitonmonteiro.model.NotificationModel;
import io.github.cleitonmonteiro.model.SubscriptionModel;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
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

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try {
            MongoClientURI mongoClientURI = new MongoClientURI("mongodb://root:rootpass@localhost:27017/mobile?authSource=admin");
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
