package cz.graphql.tutorial.web;

import com.coxautodev.graphql.tools.SchemaParser;
import cz.graphql.tutorial.schema.Query;
import graphql.schema.GraphQLSchema;
import graphql.servlet.SimpleGraphQLServlet;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;


@WebServlet(urlPatterns = "/graphql")
public class GraphQLServlet extends SimpleGraphQLServlet {

    @Autowired
    public GraphQLServlet(Query query) {
        super(buildSchema(query));
    }

    private static GraphQLSchema buildSchema(Query query) {
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(query)
                .build()
                .makeExecutableSchema();
    }
}
