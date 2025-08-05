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
        return response.getBody().as(responseClass);
    }

    public T readEntityWithData() {
        return response.getBody().jsonPath().getObject("data", responseClass);
    }

    public List<T> readEntities() {
        return response.getBody().jsonPath().getList("data", responseClass);
    }

    public ResponseWrapper<T> expectingStatusCode(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode, "Response code differs");
        return this;
    }

    public ResponseWrapper<T> expectingContentType(String contentType) {
        Assert.assertEquals(response.getContentType(), contentType, "Content type differs");
        return this;
    }

    public List<T> readIncludedEntities() {
        return response.getBody().jsonPath().getList("included", responseClass);
    }

    public List<T> readListEntity() {
        return response.getBody().jsonPath().getList("", responseClass);
    }


    public List readIncluded(Class readClass) {
        return response.getBody().jsonPath().getList("included", readClass);
    }

//    public List<ErrorBody> readErrorList() {
//        return response.getBody().jsonPath().getList("errors", ErrorBody.class);
//    }
//
//    public ErrorBody readError() {
//        var errorList = readErrorList();
//        assertNotNull(errorList);
//        assertEquals(errorList.size(), 1);
//        return errorList.get(0);
//    }
}
