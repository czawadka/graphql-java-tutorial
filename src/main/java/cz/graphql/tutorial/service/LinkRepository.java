package cz.graphql.tutorial.service;

import com.mongodb.client.MongoCollection;
import cz.graphql.tutorial.schema.Link;
import cz.graphql.tutorial.schema.LinkFilter;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.regex;

@Component
public class LinkRepository {
    public static final String FIELD_URL = "url";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_USER_ID = "userId";
    private final MongoCollection<Document> links;

    @Autowired
    public LinkRepository(@Qualifier("links") MongoCollection<Document> links) {
        this.links = links;
    }

    public Link findById(String id) {
        Document doc = links.find(eq("_id", new ObjectId(id))).first();
        return link(doc);
    }

    public List<Link> getLinks(LinkFilter linkFilter) {
        final Bson filter = buildFilter(linkFilter);
        return links.find(filter)
                .map(this::link)
                .into(new ArrayList<>());
    }

    public Link saveLink(Link link) {
        Document doc = new Document()
            .append(FIELD_URL, link.getUrl())
            .append(FIELD_DESCRIPTION, link.getDescription())
            .append(FIELD_USER_ID, link.getUserId());
        links.insertOne(doc);
        return link(doc);
    }

    private Link link(Document doc) {
        return new Link(
                doc.get("_id").toString(),
                doc.getString(FIELD_URL),
                doc.getString(FIELD_DESCRIPTION),
                doc.getString(FIELD_USER_ID)
        );
    }

    private Bson buildFilter(LinkFilter linkFilter) {
        Bson containsCondition = new BsonDocument(); // empty filter
        if (linkFilter != null) {
            String containsPattern = linkFilter.getContains();
            if (containsPattern != null && !containsPattern.isEmpty()) {
                Bson descriptionCondition = regex("description", ".*" + containsPattern + ".*", "i");
                Bson urlCondition = regex("url", ".*" + containsPattern + ".*", "i");
                containsCondition = or(descriptionCondition, urlCondition);
            }
        }
        return containsCondition;
    }
}
