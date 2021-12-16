package io.github.cleitonmonteiro.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.github.cleitonmonteiro.helper.GeolocationHelper;
import io.github.cleitonmonteiro.helper.GsonHelper;
import io.github.cleitonmonteiro.model.LocationKafkaMessage;
import io.github.cleitonmonteiro.model.LocationModel;
import io.github.cleitonmonteiro.model.NotificationModel;
import io.github.cleitonmonteiro.model.SubscriptionModel;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LocationBolt  extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(LocationBolt.class);
    private OutputCollector collector;
    private MongoDatabase mobileDatabase;
    private CodecRegistry pojoCodecRegistry;
    private String mongoHost;

    public LocationBolt(String mongoHost) {
        this.mongoHost = mongoHost;
    }

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try {
            MongoClientURI mongoClientURI = new MongoClientURI(String.format("mongodb://root:rootpass@%s:27017/mobile?authSource=admin", mongoHost));
            MongoClient mongoClient = new MongoClient(mongoClientURI);
            LOG.info("Connect to mongodb!");
            mobileDatabase = mongoClient.getDatabase("mobile");
            PojoCodecProvider codecProvider = PojoCodecProvider.builder()
                    .automatic(true)
                    .build();
            pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromProviders(codecProvider));
        } catch (Exception e) {
            LOG.info("Cannot connect to mongodb!" + e.getMessage());
        }
    }

    private void processMobileCoordinate(Tuple tuple, LocationKafkaMessage locationKafkaMessage) {
        if (mobileDatabase != null) {
            MongoCollection<SubscriptionModel> subscriptionsCollection = mobileDatabase.withCodecRegistry(pojoCodecRegistry).getCollection("subscriptions", SubscriptionModel.class);
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("mobileId", locationKafkaMessage.getMobileId());
            MongoCursor<SubscriptionModel> cursor = subscriptionsCollection.find(searchQuery).iterator();
            try {
                while (cursor.hasNext()) {
                    SubscriptionModel currentSub = cursor.next();
                    double distance = GeolocationHelper.distanceBetween(locationKafkaMessage, new LocationModel(currentSub.getLatitude(), currentSub.getLongitude()));
                    NotificationModel notification;

                    if (distance <= currentSub.getDistanceToNotifier()) {
                        notification = new NotificationModel(currentSub.getUserId(), currentSub.getMobileId(), false);
                        collector.emit(tuple, new Values(notification));
                        collector.ack(tuple);
                    }

                    if (currentSub.isTrack()) {
                        notification = new NotificationModel(currentSub.getUserId(), currentSub.getMobileId(), true);
                        collector.emit(tuple, new Values(notification));
                        collector.ack(tuple);
                    }
                }
            } finally {
                cursor.close();
            }
        } else {
            LOG.info("Cannot process coordinate because dont have access to database.");
        }
    }

    public void execute(Tuple tuple) {
        try {
            Gson gson = GsonHelper.instance();
            String locationJSON = tuple.getString(0);
            LOG.info(String.format("JSON: %s", locationJSON));
            LocationKafkaMessage locationKafkaMessage = gson.fromJson(locationJSON, LocationKafkaMessage.class);
            processMobileCoordinate(tuple, locationKafkaMessage);
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("notification"));
    }
}
