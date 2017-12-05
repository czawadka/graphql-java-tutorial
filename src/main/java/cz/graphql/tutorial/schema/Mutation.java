package cz.graphql.tutorial.schema;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import cz.graphql.tutorial.service.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Mutation implements GraphQLMutationResolver {
    private final LinkRepository linkRepository;

    @Autowired
    public Mutation(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public Link createLink(String url, String description) {
        Link newLink = new Link(null, url, description);
        linkRepository.saveLink(newLink);
        return newLink;
    }
}
