package io.github.cleitonmonteiro.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.google.gson.Gson;
import io.github.cleitonmonteiro.helper.GsonHelper;
import io.github.cleitonmonteiro.model.NotificationModel;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

public class NotifierBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory.getLogger(io.github.cleitonmonteiro.bolts.NotifierBolt.class);
    private OutputCollector collector;
    private final String KAFKA_TOPIC = "device-notifier";
    private String mongoHost;
    private String kafkaIP;
    private KafkaProducer<String, String> kafkaProducer;

    public NotifierBolt(String mongoHost, String kafkaIP) {
        this.mongoHost = mongoHost;
        this.kafkaIP = kafkaIP;
    }

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        try {
            Properties props = new Properties();
            props.put("metadata.broker.list", kafkaIP);
            props.put("bootstrap.servers", kafkaIP);
            props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            props.put("request.required.acks", "1");
            kafkaProducer = new KafkaProducer<String, String>(props);
            LOG.info("Create a kafka producer succeed!");
        } catch (Exception e) {
            LOG.info("Cannot create a kafka producer! Err: " + e.getMessage());
        }
    }

    public void execute(Tuple tuple) {
        try {
            NotificationModel notification = (NotificationModel) tuple.getValue(0);
            LOG.info("Notification : " + notification);

            if (kafkaProducer != null) {
                Gson gson = GsonHelper.instance();
                String notificationJSON = gson.toJson(notification);
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(KAFKA_TOPIC, "notification", notificationJSON);
                Callback callback = new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata data, Exception ex) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }
                        LOG.info("Kafka message sent to " + data.topic() + " | partition " + data.partition() + "| offset " + data.offset() + "| tempo " + data.timestamp());
                    }
                };
                kafkaProducer.send(record, callback).get();
            } else {
                LOG.info("Kafka producer === null");
            }
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
