package api.client;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.Objects.requireNonNull;

/**
 * Wrapper class for REST Assured responses with type safety and logging.
 * Provides convenient methods for response validation and entity extraction.
 */
public final class ResponseWrapper {

    private static final Logger LOGGER = LogManager.getLogger(ResponseWrapper.class);
    
    private final Response response;

    /**
     * Creates a new ResponseWrapper with the given response.
     *
     * @param response the REST Assured response
     */
    public ResponseWrapper(final Response response) {
        this.response = response;
        LOGGER.debug("ResponseWrapper created for response with status: {}", response.getStatusCode());
    }

    /**
     * Gets the underlying REST Assured response.
     *
     * @return the response
     */
    public Response getResponse() {
        requireNonNull(response, "Response cannot be null");
        return response;
    }

    /**
     * Reads and deserializes the response body to the expected entity type.
     *
     * @param responseClass the expected response entity class
     * @param <T> the type of the response entity
     * @return the deserialized entity
     * @throws IllegalStateException if the response body is empty or cannot be deserialized
     */
    public <T> T readEntity(final Class<T> responseClass) {
        String body = response.getBody().asString();
        if (body == null || body.isEmpty()) {
            LOGGER.error("Response body is empty; cannot map to {}", responseClass.getSimpleName());
            throw new IllegalStateException("Response body is empty; cannot map to " + responseClass.getSimpleName());
        }
        
        try {
            T entity = response.getBody().as(responseClass);
            LOGGER.debug("Successfully deserialized response to {}: {}", responseClass.getSimpleName(), entity);
            return entity;
        } catch (Exception e) {
            LOGGER.error("Failed to deserialize response body to {}: {}", responseClass.getSimpleName(), body, e);
            throw new IllegalStateException("Failed to deserialize response body to " + responseClass.getSimpleName(), e);
        }
    }

    /**
     * Asserts that the response has the expected status code.
     *
     * @param statusCode the expected status code
     * @return this ResponseWrapper for method chaining
     */
    public ResponseWrapper expectingStatusCode(final int statusCode) {
        int actualStatusCode = response.getStatusCode();
        LOGGER.debug("Asserting status code: expected={}, actual={}", statusCode, actualStatusCode);
        
        if (actualStatusCode != statusCode) {
            String message = String.format("Response status code differs. Expected: %d, Actual: %d", statusCode, actualStatusCode);
            LOGGER.error(message);
            throw new AssertionError(message);
        }
        return this;
    }

    /**
     * Reads and deserializes the response body as an error entity.
     *
     * @param errorClass the expected error entity class
     * @param <E> the error entity type
     * @return the deserialized error entity
     * @throws IllegalStateException if the response body is empty or cannot be deserialized
     */
    public <E> E readError(final Class<E> errorClass) {
        String body = response.getBody().asString();
        if (body == null || body.isEmpty()) {
            LOGGER.error("Response body is empty; cannot map to error class {}", errorClass.getSimpleName());
            throw new IllegalStateException("Response body is empty; cannot map to " + errorClass.getSimpleName());
        }

        try {
            E errorEntity = response.getBody().as(errorClass);
            LOGGER.debug("Successfully deserialized error response to {}: {}", errorClass.getSimpleName(), errorEntity);
            return errorEntity;
        } catch (Exception e) {
            LOGGER.error("Failed to parse error body to {}. Raw response: {}", errorClass.getSimpleName(), body, e);
            throw new IllegalStateException("Failed to parse error body to " + errorClass.getSimpleName()
                    + ". Raw response: " + body, e);
        }
    }

    /**
     * Gets the response status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return response.getStatusCode();
    }

    /**
     * Gets the response body as a string.
     *
     * @return the response body
     */
    public String getBodyAsString() {
        return response.getBody().asString();
    }
}
