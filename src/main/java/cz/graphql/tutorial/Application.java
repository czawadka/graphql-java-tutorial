package cz.graphql.tutorial;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
@ServletComponentScan
public class Application {

    @Bean
    public MongoCollection<Document> linksCollection(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection("links");
    }

    @Bean
    public MongoDatabase getDefaultMongoDatabase(MongoClient mongoClient, MongoProperties mongoProperties) {
        return mongoClient.getDatabase(mongoProperties.getMongoClientDatabase());
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}

