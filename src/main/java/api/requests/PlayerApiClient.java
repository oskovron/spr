package api.requests;

import api.client.Configuration;
import api.client.ResponseWrapper;
import api.client.RestClient;
import api.model.request.Player;
import api.model.response.PlayerResponse;
import api.model.response.PlayersResponse;
import common.PropertiesReader;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static common.Properties.BASE_URL;
import static io.restassured.RestAssured.given;

/**
 * API client for Player-related operations.
 * Provides methods for CRUD operations on players with Allure step annotations.
 */
public final class PlayerApiClient extends RestClient {
    
    private static final Logger LOGGER = LogManager.getLogger(PlayerApiClient.class);

    @Override
    protected Configuration defaultConfiguration() {
        String baseUrl = PropertiesReader.getProperty(BASE_URL);
        LOGGER.debug("Creating configuration with base URL: {}", baseUrl);
        
        return Configuration.builder()
                .servicePath(baseUrl)
                .contentType("application/json")
                .build();
    }

    @Step("Create player with editor: {editor}")
    public ResponseWrapper createPlayer(final String editor, final Player player) {
        LOGGER.info("Creating player with editor: {}, player: {}", editor, player);
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("age", player.getAge());
        queryParams.put("gender", player.getGender());
        queryParams.put("login", player.getLogin());
        queryParams.put("password", player.getPassword());
        queryParams.put("role", player.getRole());
        queryParams.put("screenName", player.getScreenName());
        
        Response response = given()
                .spec(getRequestSpecification())
                .header("editor", editor)
                .queryParams(queryParams)
                .post("/player/create");
        
        return new ResponseWrapper(response);
    }

    @Step("Get player with ID: {playerId}")
    public ResponseWrapper getPlayer(final Integer playerId) {
        LOGGER.info("Getting player with ID: {}", playerId);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("playerId", playerId);
        
        Response response = given()
                .spec(getRequestSpecification())
                .body(requestBody)
                .post("/player/get");
        
        return new ResponseWrapper(response);
    }

    @Step("Get all players")
    public ResponseWrapper getAllPlayers() {
        LOGGER.info("Getting all players");
        
        Response response = given()
                .spec(getRequestSpecification())
                .get("/player/getAll");
        
        return new ResponseWrapper(response);
    }

    @Step("Update player with editor: {editor}, playerId: {playerId}")
    public ResponseWrapper updatePlayer(final String editor, final Integer playerId, final Player updatePlayer) {
        LOGGER.info("Updating player with editor: {}, playerId: {}, player: {}", editor, playerId, updatePlayer);
        
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("editor", editor);
        pathParams.put("id", playerId);
        
        Response response = given()
                .spec(getRequestSpecification())
                .pathParams(pathParams)
                .body(updatePlayer)
                .patch("/player/update/{editor}/{id}");
        
        return new ResponseWrapper(response);
    }

    @Step("Delete player with editor: {editor}, playerId: {playerId}")
    public Response deletePlayer(final String editor, final Integer playerId) {
        LOGGER.info("Deleting player with editor: {}, playerId: {}", editor, playerId);
        
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("editor", editor);
        pathParams.put("id", playerId);
        
        return given()
                .spec(getRequestSpecification())
                .pathParams(pathParams)
                .delete("/player/delete/{editor}/{id}");
    }
}
