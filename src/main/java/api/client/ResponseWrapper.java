package api.client;

import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;

import static org.testng.Assert.assertNotNull;

public class ResponseWrapper<T> {

    private final Response response;
    private final Class<T> responseClass;

    ResponseWrapper(Response response, Class<T> responseClass) {
        this.response = response;
        this.responseClass = responseClass;
    }

    public Response getResponse() {
        assertNotNull(response);
        return response;
    }

    public T readEntity() {
        String body = response.getBody().asString();
        if (body == null || body.isEmpty()) {
            throw new IllegalStateException("Response body is empty; cannot map to " + responseClass.getSimpleName());
        }
        return response.getBody().as(responseClass);
    }

    public ResponseWrapper<T> expectingStatusCode(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode, "Response code differs");
        return this;
    }

    public ResponseWrapper<T> expectingContentType(String contentType) {
        Assert.assertEquals(response.getContentType(), contentType, "Content type differs");
        return this;
    }

    public List<T> readListEntity() {
        return response.getBody().jsonPath().getList("", responseClass);
    }

}
