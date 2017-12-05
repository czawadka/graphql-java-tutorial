package cz.graphql.tutorial.service;

import com.mongodb.client.MongoCollection;
import cz.graphql.tutorial.schema.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.mongodb.client.model.Filters.eq;

@Component
public class UserRepository {
    private final MongoCollection<Document> users;

    @Autowired
    public UserRepository(@Qualifier("users") MongoCollection<Document> users) {
        this.users = users;
    }

    public User findByEmail(String email) {
        Document doc = users.find(eq("email", email)).first();
        return user(doc);
    }

    public User findById(String id) {
        Document doc = users.find(eq("_id", new ObjectId(id))).first();
        return user(doc);
    }

    public User saveUser(User user) {
        Document doc = new Document()
            .append("name", user.getName())
            .append("email", user.getEmail())
            .append("password", user.getPassword());
        users.insertOne(doc);
        return user(doc);
    }

    private User user(Document doc) {
        return new User(
                doc.get("_id").toString(),
                doc.getString("name"),
                doc.getString("email"),
                doc.getString("password"));
    }
}
