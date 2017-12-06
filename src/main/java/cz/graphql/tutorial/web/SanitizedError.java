package cz.graphql.tutorial.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import graphql.ExceptionWhileDataFetching;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;

import java.util.List;

public class SanitizedError extends ExceptionWhileDataFetching {

    public SanitizedError(ExceptionWhileDataFetching inner) {
        super(ExecutionPath.fromList(inner.getPath()), inner.getException(), firstLocation(inner.getLocations()));
    }

    private static SourceLocation firstLocation(List<SourceLocation> sourceLocations) {
        if (sourceLocations == null) {
            return null;
        }
        return  sourceLocations.stream().findFirst().orElse(null);
    }

    @Override
    @JsonIgnore
    public Throwable getException() {
        return super.getException();
    }
}