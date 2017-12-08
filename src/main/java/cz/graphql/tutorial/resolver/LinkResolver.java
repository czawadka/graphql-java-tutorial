package cz.graphql.tutorial.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import cz.graphql.tutorial.schema.Link;
import cz.graphql.tutorial.schema.User;
import cz.graphql.tutorial.service.UserRepository;
import graphql.execution.batched.Batched;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

@Component
public class LinkResolver implements GraphQLResolver<Link> {
    private final UserRepository userRepository;

    @Autowired
    public LinkResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Batched
    public List<User> postedBy(List<Link> links) {
        List<String> linkIds = links.stream()
                .map(Link::getUserId)
                .filter(Objects::nonNull)
                .collect(toList());

        final Map<String, User> userMap = userRepository.findByIds(linkIds).stream()
                .collect(Collectors.toMap(User::getId, identity()));

        return links.stream()
                .map(Link::getUserId)
                .map(userId -> userId != null ? userMap.get(userId) : null)
                .collect(toList());
    }
}
