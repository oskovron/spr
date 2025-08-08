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

}
