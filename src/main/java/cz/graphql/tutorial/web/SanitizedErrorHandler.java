package cz.graphql.tutorial.web;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.servlet.GraphQLErrorHandler;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class SanitizedErrorHandler implements GraphQLErrorHandler {
    @Override
    public List<GraphQLError> processErrors(List<GraphQLError> errors) {
        return errors.stream()
                .map(error -> {
                    if (!(error instanceof ExceptionWhileDataFetching)) {
                        return error;
                    }
                    return new SanitizedError((ExceptionWhileDataFetching) error);
                })
                .collect(toList());
    }
}
