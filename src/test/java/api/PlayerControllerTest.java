package api;

import api.client.ResponseWrapper;
import api.model.Player;
import api.requests.PlayerApiClient;
import common.PropertiesReader;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.TestDataGenerator;

import java.util.ArrayList;
import java.util.List;

public class PlayerControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(PlayerControllerTest.class);
    private PlayerApiClient apiClient;
    private List<Integer> createdPlayerIds;

    @BeforeClass
    public void setUp() {
        logger.info("Setting up PlayerControllerTest");
        apiClient = new PlayerApiClient();
//        config = ConfigManager.getInstance();
        createdPlayerIds = new ArrayList<>();
    }

    @AfterClass
    public void tearDown() {
        logger.info("Cleaning up created players");
        // Clean up created players to avoid test data pollution
        for (Integer playerId : createdPlayerIds) {
            try {
//                apiClient.deletePlayer(config.getSupervisorLogin(), playerId); //todo delete!
                logger.info("Cleaned up player with ID: {}", playerId);
            } catch (Exception e) {
                logger.warn("Failed to clean up player with ID: {}", playerId, e);
            }
        }
    }

    @Test(description = "Create player with valid data using supervisor")
    public void testCreatePlayerWithValidDataUsingSupervisor() {
        Player player = TestDataGenerator.generateValidPlayer();

        ResponseWrapper<Player> response = apiClient.createPlayer(PropertiesReader.getProperty("default.supervisor.login"), player);

        response.expectingStatusCode(200);
//        Assert.assertNotNull(response.asString(), "Response body should not be null");
//
//        // Extract player ID from response for cleanup
//        String responseBody = response..asString();
//        if (responseBody.contains("id")) {
//            // Parse player ID from response for cleanup
//            try {
//                int playerId = Integer.parseInt(responseBody.replaceAll("[^0-9]", ""));
//                createdPlayerIds.add(playerId);
//            } catch (NumberFormatException e) {
//                logger.warn("Could not parse player ID from response: {}", responseBody);
//            }
//        }
    }
}
