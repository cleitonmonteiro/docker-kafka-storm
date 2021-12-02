package io.github.cleitonmonteiro;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.github.cleitonmonteiro.helper.GeolocationHelper;
import io.github.cleitonmonteiro.model.LocationKafkaMessage;
import io.github.cleitonmonteiro.model.LocationModel;
import io.github.cleitonmonteiro.model.MobileModel;
import io.github.cleitonmonteiro.model.SubscriptionModel;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class Demo {

    public static void main(String[] args) {
        demoKafkaMessage();
    }

    public static void demoKafkaMessage() {
        try {
            MongoClientURI mongoClientURI = new MongoClientURI("mongodb://root:rootpass@localhost:27017/mobile?authSource=admin");
            MongoClient mongoClient = new MongoClient(mongoClientURI);
            MongoDatabase database = mongoClient.getDatabase("mobile");
            // TODO: Add this to main code
            PojoCodecProvider codecProvider = PojoCodecProvider.builder()
                    .automatic(true)
                    .build();
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromProviders(codecProvider));
            MongoCollection<SubscriptionModel> subscriptionsCollection = database.withCodecRegistry(pojoCodecRegistry).getCollection("subscriptions", SubscriptionModel.class);
            MongoCollection<MobileModel> mobileCollection = database.withCodecRegistry(pojoCodecRegistry).getCollection("mobiles", MobileModel.class);

            LocationKafkaMessage locationKafkaMessage = new LocationKafkaMessage();
            locationKafkaMessage.setLatitude(-4.560945);
            locationKafkaMessage.setLongitude(-37.770139);
            locationKafkaMessage.setAccuracy(10.0);
            locationKafkaMessage.setProvider("FUSED");
            locationKafkaMessage.setMobileId("61a82b333ebb7d3bafde8ac5");

            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("mobileId", locationKafkaMessage.getMobileId());
            MongoCursor<SubscriptionModel> cursor = subscriptionsCollection.find(searchQuery).iterator();
//            MongoCursor<MobileModel> cursor = mobileCollection.find(searchQuery).iterator();
            try {
                while (cursor.hasNext()) {
                    SubscriptionModel current = cursor.next();
                    double distance = GeolocationHelper.distanceBetween(locationKafkaMessage, new LocationModel(current.getLatitude(), current.getLongitude()));
                    System.out.println(current.toString());
                    System.out.println("Distance: " + distance);
                    if (distance <= current.getDistanceToNotifier()) {
                        System.out.println("Notifier by distance");
                    }

                    if (current.isTrack()) {
                        System.out.println("Notifier by track");
                    }
                }
            } finally {
                cursor.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
