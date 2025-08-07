package api.client;

import io.restassured.response.Response;
import org.testng.Assert;

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

    public <E> E readError(Class<E> errorClass) {
        String body = response.getBody().asString();
        if (body == null || body.isEmpty()) {
            throw new IllegalStateException("Response body is empty; cannot map to " + errorClass.getSimpleName());
        }

        try {
            return response.getBody().as(errorClass);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse error body to " + errorClass.getSimpleName()
                    + ". Raw response: " + body, e);
        }
    }

}
