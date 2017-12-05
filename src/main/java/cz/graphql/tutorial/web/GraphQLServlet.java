package cz.graphql.tutorial.web;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.coxautodev.graphql.tools.SchemaParser;
import cz.graphql.tutorial.schema.User;
import cz.graphql.tutorial.service.UserRepository;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLContext;
import graphql.servlet.SimpleGraphQLServlet;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;


@WebServlet(urlPatterns = "/graphql")
public class GraphQLServlet extends SimpleGraphQLServlet {
    private final UserRepository userRepository;

    @Autowired
    public GraphQLServlet(UserRepository userRepository, Collection<GraphQLResolver<?>> resolvers) {
        super(buildSchema(resolvers));
        this.userRepository = userRepository;
    }

    @Override
    protected GraphQLContext createContext(Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
        User user = request
                .map(req -> req.getHeader("Authorization"))
                .filter(id -> !id.isEmpty())
                .map(id -> id.replace("Bearer ", ""))
                .map(userRepository::findById)
                .orElse(null);
        return new AuthContext(user, request, response);
    }

    private static GraphQLSchema buildSchema(Collection<GraphQLResolver<?>> resolvers) {
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(new ArrayList<>(resolvers))
                .build()
                .makeExecutableSchema();
    }
}
