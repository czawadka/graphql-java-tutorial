package cz.graphql.tutorial.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import cz.graphql.tutorial.schema.Link;
import cz.graphql.tutorial.schema.User;
import cz.graphql.tutorial.schema.Vote;
import cz.graphql.tutorial.service.LinkRepository;
import cz.graphql.tutorial.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VoteResolver implements GraphQLResolver<Vote> {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    @Autowired
    public VoteResolver(LinkRepository linkRepository, UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    public User user(Vote vote) {
        return userRepository.findById(vote.getUserId());
    }

    public Link link(Vote vote) {
        return linkRepository.findById(vote.getLinkId());
    }
}
