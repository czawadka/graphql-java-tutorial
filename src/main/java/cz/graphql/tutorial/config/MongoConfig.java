package cz.graphql.tutorial.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {
    @Bean
    public MongoCollection<Document> linksCollection(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection("links");
    }

    @Bean
    public MongoDatabase getDefaultMongoDatabase(MongoClient mongoClient, MongoProperties mongoProperties) {
        return mongoClient.getDatabase(mongoProperties.getMongoClientDatabase());
    }

}
