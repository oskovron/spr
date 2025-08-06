package api.client;

import api.requests.PlayerApiClient;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class RestClient {
    protected static final Logger logger = LoggerFactory.getLogger(PlayerApiClient.class);
    protected Configuration configuration;
    private RequestSpecification requestSpecification;
    private RestAssuredConfig restAssuredConfig = RestAssured.config()
            .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());

    protected abstract Configuration defaultConfiguration();

    public RestClient() {
        getSession();
    }

    public void getSession() {
        configuration = defaultConfiguration();

        //todo: logging
//        var responseFilter = LogManager.getLogger().getLevel() == ERROR ?
//                ErrorLoggingFilter.logErrorsTo(errorStream) :
//                ResponseLoggingFilter.logResponseTo(printStream);


        requestSpecification = new RequestSpecBuilder()
                .setConfig(restAssuredConfig)
                .setBaseUri(configuration.getServicePath())
                .setContentType(configuration.getContentType())
//                .log(io.restassured.filter.log.LogDetail.ALL)
//                .addHeaders(configuration.getHeaders())
                .build();
    }

    public <F> ResponseWrapper<F> get(String path, Class<F> responseClass) {
        Response response = given()
                .spec(requestSpecification)
                .get(path);
        return new ResponseWrapper<>(response, responseClass);
    }

    public <F> ResponseWrapper<F> get(String path, String pathParam, Map<String, Object> queryParams, Class<F> responseClass) {
        Response response = given()
                .spec(requestSpecification)
                .pathParam(pathParam, pathParam)
                .queryParams(queryParams)
                .get(path);
        return new ResponseWrapper<>(response, responseClass);
    }

    public <T, F> ResponseWrapper<F> post(String path, T payload, Class<F> responseClass) {
        Response response = given()
                .spec(requestSpecification).body(payload).post(path);
        return new ResponseWrapper<>(response, responseClass);
    }

    public <T, F> ResponseWrapper<F> patch(String path, Map<String, Object> pathParams, T payload, Class<F> responseClass) {
        Response response = given()
                .spec(requestSpecification)
                .pathParams(pathParams)
                .body(payload)
                .patch(path);
        return new ResponseWrapper<>(response, responseClass);
    }

    public <T> Response delete(String path, Map<String, Object> pathParam, T payload) {
        return given()
                .spec(requestSpecification)
                .pathParams(pathParam)
                .body(payload)
                .delete(path);

    }
}
