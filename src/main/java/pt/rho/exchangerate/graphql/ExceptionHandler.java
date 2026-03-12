package pt.rho.exchangerate.graphql;

import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.RestClientException;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

import pt.rho.exchangerate.exception.ApiException;

@ControllerAdvice
public class ExceptionHandler {

    @GraphQlExceptionHandler(ApiException.class)
    public GraphQLError handleApiException(ApiException ex, DataFetchingEnvironment environment) {
        return graphQlError(ex.getHttpStatus().name(), ex.getMessage(), environment);
    }

    @GraphQlExceptionHandler(RestClientException.class)
    public GraphQLError handleRestClientException(RestClientException ex, DataFetchingEnvironment environment) {
        return graphQlError("BAD_GATEWAY", "Failed to retrieve data from external exchange rate provider",
                environment);
    }

    @GraphQlExceptionHandler(MissingServletRequestParameterException.class)
    public GraphQLError handleMissingParameter(MissingServletRequestParameterException ex,
            DataFetchingEnvironment environment) {
        return graphQlError("BAD_REQUEST",
                "Required request parameter '%s' is missing".formatted(ex.getParameterName()), environment);
    }

    @GraphQlExceptionHandler(Exception.class)
    public GraphQLError handleGenericException(Exception ex, DataFetchingEnvironment environment) {
        return graphQlError("INTERNAL_SERVER_ERROR", "An unexpected error occurred", environment);
    }

    private GraphQLError graphQlError(String errorType, String message, DataFetchingEnvironment environment) {
        return GraphqlErrorBuilder.newError(environment)
                .message(message)
                .extensions(java.util.Map.of("classification", errorType))
                .build();
    }
}
