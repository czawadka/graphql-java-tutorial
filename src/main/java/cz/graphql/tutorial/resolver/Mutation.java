package cz.graphql.tutorial.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import cz.graphql.tutorial.schema.AuthData;
import cz.graphql.tutorial.schema.Link;
import cz.graphql.tutorial.schema.SigninPayload;
import cz.graphql.tutorial.schema.User;
import cz.graphql.tutorial.schema.Vote;
import cz.graphql.tutorial.service.LinkRepository;
import cz.graphql.tutorial.service.UserRepository;
import cz.graphql.tutorial.service.VoteRepository;
import cz.graphql.tutorial.web.AuthContext;
import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class Mutation implements GraphQLMutationResolver {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public Mutation(LinkRepository linkRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    public Link createLink(String url, String description, DataFetchingEnvironment env) {
        AuthContext context = env.getContext();
        Link newLink = new Link(null, url, description, context.getUser().getId());
        return linkRepository.saveLink(newLink);
    }

    public User createUser(String name, AuthData auth) {
        User newUser = new User(name, auth.getEmail(), auth.getPassword());
        return userRepository.saveUser(newUser);
    }

    public SigninPayload signinUser(AuthData auth) throws IllegalAccessException {
        User user = userRepository.findByEmail(auth.getEmail());
        if (user.getPassword().equals(auth.getPassword())) {
            return new SigninPayload(user.getId(), user);
        }
        throw new GraphQLException("Invalid credentials");
    }

    public Vote createVote(String linkId, String userId) {
        ZonedDateTime now = Instant.now().atZone(ZoneOffset.UTC);
        return voteRepository.saveVote(new Vote(now, userId, linkId));
    }
}
