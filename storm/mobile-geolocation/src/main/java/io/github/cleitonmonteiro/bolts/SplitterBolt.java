package io.github.cleitonmonteiro.bolts;

import com.google.gson.Gson;
import io.github.cleitonmonteiro.model.LocationModel;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SplitterBolt  extends BaseRichBolt {
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
            LocationModel location = gson.fromJson(locationJSON, LocationModel.class);

//            LOG.info("SplitterBolt -> provider" + location.provider);
            LOG.info("SplitterBolt -> message" + message);
            LOG.info("SplitterBolt -> latitude" + location.getLatitude());

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
