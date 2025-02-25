package com.biggis.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.biggis.storm.model.Location;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * SplitterBolt
 */
public class SplitterBolt extends BaseRichBolt {

    private static final Logger LOG = LoggerFactory.getLogger(SplitterBolt.class);
    private OutputCollector collector;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {

        this.collector = outputCollector;
    }

    public void execute(Tuple tuple) {
        LOG.info("SplitterBolt -> Tuple: " + tuple.toString());

        // get tuples from KafkaSpout
        try {
            Gson gson = new Gson();
            String locationJSON = tuple.getString(0);
            String message = tuple.getStringByField("message");
            Location location = gson.fromJson(locationJSON, Location.class);

            LOG.info("SplitterBolt -> provider" + location.provider);
            LOG.info("SplitterBolt -> message" + message);
            LOG.info("SplitterBolt -> latitude" + location.latitude);

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
