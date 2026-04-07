package pt.rho.exchangerate.web;

import static org.assertj.core.api.Assertions.assertThat;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.RestClientException;

import pt.rho.exchangerate.exception.ApiException;
import pt.rho.exchangerate.exception.InvalidCurrencyException;

@ExtendWith(MockitoExtension.class)
class GraphQLErrorHandlerTests {

    @Mock
    private DataFetchingEnvironment environment;

    private final GraphQLErrorHandler graphQLErrorHandler = new GraphQLErrorHandler();

    @Test
    @DisplayName("Should map ApiException to GraphQLError with HTTP status classification")
    void shouldHandleApiException() {
        ApiException exception = new InvalidCurrencyException("Currency must have exactly 3 characters");

        GraphQLError error = graphQLErrorHandler.handleApiException(exception, environment);

        assertThat(error.getMessage()).isEqualTo("Currency must have exactly 3 characters");
        assertThat(error.getExtensions())
                .containsEntry("classification", "BAD_REQUEST");
    }

    @Test
    @DisplayName("Should map RestClientException to bad gateway GraphQLError")
    void shouldHandleRestClientException() {
        RestClientException exception = new RestClientException("Provider unavailable");

        GraphQLError error = graphQLErrorHandler.handleRestClientException(exception, environment);

        assertThat(error.getMessage())
                .isEqualTo("Failed to retrieve data from external exchange rate provider");
        assertThat(error.getExtensions())
                .containsEntry("classification", "BAD_GATEWAY");
    }

    @Test
    @DisplayName("Should map MissingServletRequestParameterException to bad request GraphQLError")
    void shouldHandleMissingParameterException() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("amount", "BigDecimal");

        GraphQLError error = graphQLErrorHandler.handleMissingParameter(exception, environment);

        assertThat(error.getMessage())
                .isEqualTo("Required request parameter 'amount' is missing");
        assertThat(error.getExtensions())
                .containsEntry("classification", "BAD_REQUEST");
    }

    @Test
    @DisplayName("Should map unexpected exception to internal server error GraphQLError")
    void shouldHandleGenericException() {
        Exception exception = new RuntimeException("Unexpected");

        GraphQLError error = graphQLErrorHandler.handleGenericException(exception, environment);

        assertThat(error.getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(error.getExtensions())
                .containsEntry("classification", "INTERNAL_SERVER_ERROR");
    }
}