package cz.graphql.tutorial.schema;

import com.coxautodev.graphql.tools.GraphQLRootResolver;
import cz.graphql.tutorial.service.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Query implements GraphQLRootResolver {
    private final LinkRepository linkRepository;

    @Autowired
    public Query(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public List<Link> link() {
        return linkRepository.getAllLinks();
    }
}