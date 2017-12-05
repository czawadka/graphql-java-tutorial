package cz.graphql.tutorial.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import cz.graphql.tutorial.schema.SigninPayload;
import cz.graphql.tutorial.schema.User;
import org.springframework.stereotype.Component;

@Component
public class SigninPayloadResolver implements GraphQLResolver<SigninPayload> {

    public User user(SigninPayload payload) {
        return payload.getUser();
    }
}
