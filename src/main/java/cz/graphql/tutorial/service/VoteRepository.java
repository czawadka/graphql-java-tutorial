package cz.graphql.tutorial.service;

import com.mongodb.client.MongoCollection;
import cz.graphql.tutorial.schema.Scalars;
import cz.graphql.tutorial.schema.Vote;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Component
public class VoteRepository {
    private final MongoCollection<Document> votes;

    @Autowired
    public VoteRepository(@Qualifier("votes") MongoCollection<Document> votes) {
        this.votes = votes;
    }

    public List<Vote> findByUserId(String userId) {
        List<Vote> list = new ArrayList<>();
        for (Document doc : votes.find(eq("userId", userId))) {
            list.add(vote(doc));
        }
        return list;
    }

    public List<Vote> findByLinkId(String linkId) {
        List<Vote> list = new ArrayList<>();
        for (Document doc : votes.find(eq("linkId", linkId))) {
            list.add(vote(doc));
        }
        return list;
    }

    public Vote saveVote(Vote vote) {
        Document doc = new Document();
        doc.append("userId", vote.getUserId());
        doc.append("linkId", vote.getLinkId());
        doc.append("createdAt", Scalars.dateTime.getCoercing().serialize(vote.getCreatedAt()));
        votes.insertOne(doc);
        return new Vote(
                doc.get("_id").toString(),
                vote.getCreatedAt(),
                vote.getUserId(),
                vote.getLinkId());
    }

    private Vote vote(Document doc) {
        return new Vote(
                doc.get("_id").toString(),
                ZonedDateTime.parse(doc.getString("createdAt")),
                doc.getString("userId"),
                doc.getString("linkId")
        );
    }
}
