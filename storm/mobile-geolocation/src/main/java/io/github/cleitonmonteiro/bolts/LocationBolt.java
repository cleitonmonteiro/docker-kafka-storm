package io.github.cleitonmonteiro.bolts;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.github.cleitonmonteiro.model.LocationKafkaMessage;
import io.github.cleitonmonteiro.model.LocationModel;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LocationBolt  extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(LocationBolt.class);
    private OutputCollector collector;
    private MongoDatabase mobileDatabase;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try {
            MongoClientURI mongoClientURI = new MongoClientURI("mongodb://root:rootpass@localhost:27017/mobile?authSource=admin");
            MongoClient mongoClient = new MongoClient(mongoClientURI);
            mobileDatabase = mongoClient.getDatabase("mobile");
        } catch (Exception e) {
            LOG.info("Cannot connect to mongodb!" + e.getMessage());
        }
    }

    private void processMobileCoordinate(LocationKafkaMessage locationKafkaMessage) {
        if (mobileDatabase != null) {
            MongoCollection<Document> geoCollection = mobileDatabase.getCollection("geolocation");
            BasicDBObject searchQuery = new BasicDBObject();
            // searchQuery.put("name", "John");
            MongoCursor<Document> cursor = geoCollection.find().iterator();
            try {
                while (cursor.hasNext()) {
                    System.out.println(cursor.next());
                    System.out.println("oi");
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
            Gson gson = new Gson();
            String locationJSON = tuple.getString(0);
            LocationKafkaMessage locationKafkaMessage = gson.fromJson(locationJSON, LocationKafkaMessage.class);
            processMobileCoordinate(locationKafkaMessage);
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }

        String [] words = tuple.getString(0).split("\\s+");

        for (int i = 0; i < words.length; i++) {
            LOG.info("SplitterBolt -> words: " + words[i].toString());
            // check fo non-word character and replace
            words[i] = words[i].replaceAll("[^\\w]", "");

            // emit words and acknowledge processed tuple
            collector.emit(tuple, new Values(words[i]));
            collector.ack(tuple);
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}
