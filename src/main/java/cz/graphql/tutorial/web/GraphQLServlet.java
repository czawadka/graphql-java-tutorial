package cz.graphql.tutorial.web;

import com.coxautodev.graphql.tools.SchemaParser;
import cz.graphql.tutorial.resolver.LinkResolver;
import cz.graphql.tutorial.resolver.Mutation;
import cz.graphql.tutorial.resolver.Query;
import cz.graphql.tutorial.resolver.SigninPayloadResolver;
import cz.graphql.tutorial.resolver.VoteResolver;
import cz.graphql.tutorial.schema.Link;
import cz.graphql.tutorial.schema.Scalars;
import cz.graphql.tutorial.schema.User;
import cz.graphql.tutorial.service.UserRepository;
import graphql.execution.batched.Batched;
import graphql.execution.batched.BatchedExecutionStrategy;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.servlet.DefaultExecutionStrategyProvider;
import graphql.servlet.GraphQLContext;
import graphql.servlet.SimpleGraphQLServlet;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;


@WebServlet(urlPatterns = "/graphql")
public class GraphQLServlet extends SimpleGraphQLServlet {
    private final UserRepository userRepository;

    @Autowired
    public GraphQLServlet(UserRepository userRepository, Query query, Mutation mutation, LinkResolver linkResolver,
                          SigninPayloadResolver signinPayloadResolver, VoteResolver voteResolver) {
        super(buildSchema(query, mutation, linkResolver, signinPayloadResolver, voteResolver),
                new DefaultExecutionStrategyProvider(new BatchedExecutionStrategy()), null, null, null,
                new SanitizedErrorHandler(), null, null, null);
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

    private static GraphQLSchema buildSchema(Query query, Mutation mutation, LinkResolver linkResolver, SigninPayloadResolver signinPayloadResolver, VoteResolver voteResolver) {
        return buildSchemaByTools(query, mutation, linkResolver, signinPayloadResolver, voteResolver);
    }

    private static GraphQLSchema buildSchemaByTools(Query query, Mutation mutation, LinkResolver linkResolver, SigninPayloadResolver signinPayloadResolver, VoteResolver voteResolver) {
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(Arrays.asList(query, mutation, linkResolver, signinPayloadResolver, voteResolver))
                .scalars(Scalars.dateTime)
                .build()
                .makeExecutableSchema();
    }

    private static GraphQLSchema buildSchemaByJava(Query query, Mutation mutation, LinkResolver linkResolver, SigninPayloadResolver signinPayloadResolver, VoteResolver voteResolver) {
        final InputStream is = GraphQLServlet.class.getClassLoader().getResourceAsStream("schema.graphqls");
        final InputStreamReader reader;
        try {
            reader = new InputStreamReader(is, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        final TypeDefinitionRegistry typeDefinitionRegistry = new graphql.schema.idl.SchemaParser().parse(reader);
        final RuntimeWiring runtimeWiring = newRuntimeWiring()
                .scalar(Scalars.dateTime)
                .type(newTypeWiring("Query")
                        .dataFetcher("link", env -> query.link(env.getArgument("filter")))
                )
                .type(newTypeWiring("Mutation")
                        .dataFetcher("createLink", env -> mutation.createLink(
                                env.getArgument("url"),
                                env.getArgument("description"),
                                env)
                        )
                        .dataFetcher("createUser", env -> mutation.createUser(
                                env.getArgument("name"),
                                env.getContext())
                        )
                        .dataFetcher("signinUser", env -> mutation.signinUser(
                                env.getContext())
                        )
                        .dataFetcher("createVote", env -> mutation.createVote(
                                env.getArgument("linkId"),
                                env.getArgument("userId"))
                        )
                )
                .type(newTypeWiring("Link")
                        .dataFetcher("postedBy", new DataFetcher() {
                            @Batched
                            @Override
                            public Object get(DataFetchingEnvironment env) {
                                List<Link> links = env.getSource();
                                return linkResolver.postedBy(links);
                            }
                        })
                )
                .build();
        return new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }
}
