package cz.graphql.tutorial.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import cz.graphql.tutorial.schema.Link;
import cz.graphql.tutorial.schema.User;
import cz.graphql.tutorial.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LinkResolver implements GraphQLResolver<Link> {
    private final UserRepository userRepository;

    @Autowired
    public LinkResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User postedBy(Link link) {
        if (link.getUserId() == null) {
            return null;
        }
        return userRepository.findById(link.getUserId());
    }
}
