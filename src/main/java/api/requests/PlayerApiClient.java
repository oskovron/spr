package api.requests;

import api.client.Configuration;
import api.client.ResponseWrapper;
import api.client.RestClient;
import api.model.request.Player;
import api.model.response.PlayerResponse;
import common.PropertiesReader;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class PlayerApiClient extends RestClient {
    @Override
    protected Configuration defaultConfiguration() {
        return new Configuration(PropertiesReader.getProperty("base.url"), "application/json");
    }

    public ResponseWrapper<PlayerResponse> createPlayer(String editor, Player player) {
//        logger.info("Creating player with editor: {}, player: {}", editor, player);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("age", player.getAge());
        queryParams.put("gender", player.getGender());
        queryParams.put("login", player.getLogin());
        queryParams.put("password", player.getPassword());
        queryParams.put("role", player.getRole());
        queryParams.put("screenName", player.getScreenName());
        return get(format("/player/create/{%s}", editor), editor, queryParams, PlayerResponse.class);
    }

    public ResponseWrapper<PlayerResponse> getPlayer(int playerId) {
//        logger.info("Getting player with ID: {}", playerRequest.getPlayerId());
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("playerId", playerId);
        return post("/player/get", requestBody, PlayerResponse.class);
    }
}
