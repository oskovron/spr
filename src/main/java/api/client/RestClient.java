package api.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Abstract base class for REST API clients.
 * Provides common functionality for HTTP requests with Allure integration and logging.
 */
public abstract class RestClient {
    
    private static final Logger LOGGER = LogManager.getLogger(RestClient.class);
    
    protected final Configuration configuration;
    private final RequestSpecification requestSpecification;
    private final RestAssuredConfig restAssuredConfig = RestAssured.config()
            .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());

    /**
     * Abstract method to provide default configuration for the client.
     *
     * @return the default configuration
     */
    protected abstract Configuration defaultConfiguration();

    /**
     * Constructor that initializes the REST client with configuration.
     */
    protected RestClient() {
        this.configuration = defaultConfiguration();
        this.requestSpecification = initializeRequestSpecification();
        LOGGER.info("RestClient initialized with service path: {}", configuration.getServicePath());
    }

    /**
     * Initializes the request specification with Allure integration and logging.
     *
     * @return the configured RequestSpecification
     */
    private RequestSpecification initializeRequestSpecification() {
        LOGGER.debug("Initializing request specification for service path: {}", configuration.getServicePath());
        
        return new RequestSpecBuilder()
                .setConfig(restAssuredConfig)
                .setBaseUri(configuration.getServicePath())
                .setContentType(configuration.getContentType())
                .log(io.restassured.filter.log.LogDetail.ALL)
                .addFilter(new AllureRestAssured())
                .build();
    }

    /**
     * Gets the request specification for use by subclasses.
     *
     * @return the request specification
     */
    protected RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    /**
     * Performs a GET request.
     *
     * @param path the request path
     * @param responseClass the expected response class
     * @param <F> the response type
     * @return ResponseWrapper containing the response
     */
    public <F> ResponseWrapper get(final String path, final Class<F> responseClass) {
        LOGGER.debug("Performing GET request to path: {}", path);
        
        Response response = given()
                .spec(requestSpecification)
                .get(path);
        
        LOGGER.debug("GET request completed with status code: {}", response.getStatusCode());
        return new ResponseWrapper(response);
    }

    /**
     * Performs a GET request with path parameters and query parameters.
     *
     * @param path the request path
     * @param pathParam the path parameter name
     * @param queryParams the query parameters
     * @param responseClass the expected response class
     * @param <F> the response type
     * @return ResponseWrapper containing the response
     */
    public <F> ResponseWrapper get(final String path, final String pathParam, 
                                     final Map<String, Object> queryParams, final Class<F> responseClass) {
        LOGGER.debug("Performing GET request to path: {} with pathParam: {} and queryParams: {}", 
                    path, pathParam, queryParams);
        
        Response response = given()
                .spec(requestSpecification)
                .pathParam(pathParam, pathParam)
                .queryParams(queryParams)
                .get(path);
        
        LOGGER.debug("GET request with parameters completed with status code: {}", response.getStatusCode());
        return new ResponseWrapper(response);
    }

    /**
     * Performs a POST request.
     *
     * @param path the request path
     * @param payload the request payload
     * @param responseClass the expected response class
     * @param <T> the payload type
     * @param <F> the response type
     * @return ResponseWrapper containing the response
     */
    protected <T, F> ResponseWrapper post(final String path, final T payload, final Class<F> responseClass) {
        LOGGER.debug("Performing POST request to path: {} with payload: {}", path, payload);
        
        Response response = given()
                .spec(requestSpecification)
                .body(payload)
                .post(path);
        
        LOGGER.debug("POST request completed with status code: {}", response.getStatusCode());
        return new ResponseWrapper(response);
    }

    /**
     * Performs a PATCH request.
     *
     * @param path the request path
     * @param pathParams the path parameters
     * @param payload the request payload
     * @param responseClass the expected response class
     * @param <T> the payload type
     * @param <F> the response type
     * @return ResponseWrapper containing the response
     */
    protected <T, F> ResponseWrapper patch(final String path, final Map<String, Object> pathParams, 
                                             final T payload, final Class<F> responseClass) {
        LOGGER.debug("Performing PATCH request to path: {} with pathParams: {} and payload: {}", 
                    path, pathParams, payload);
        
        Response response = given()
                .spec(requestSpecification)
                .pathParams(pathParams)
                .body(payload)
                .patch(path);
        
        LOGGER.debug("PATCH request completed with status code: {}", response.getStatusCode());
        return new ResponseWrapper(response);
    }

    /**
     * Performs a DELETE request.
     *
     * @param path the request path
     * @param pathParam the path parameters
     * @param payload the request payload
     * @param <T> the payload type
     * @return the raw Response
     */
    protected <T> Response delete(final String path, final Map<String, Object> pathParam, final T payload) {
        LOGGER.debug("Performing DELETE request to path: {} with pathParam: {} and payload: {}", 
                    path, pathParam, payload);
        
        Response response = given()
                .spec(requestSpecification)
                .pathParams(pathParam)
                .body(payload)
                .delete(path);
        
        response.then().log().all();
        LOGGER.debug("DELETE request completed with status code: {}", response.getStatusCode());
        return response;
    }
}
