package api;

import api.client.ResponseWrapper;
import api.model.request.Player;
import api.model.response.PlayerResponse;
import api.requests.PlayerApiClient;
import common.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import util.TestDataGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;

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

    @Test(description = "Create player with valid data using supervisor and verify Response body")
    public void testCreatePlayerWithValidDataUsingSupervisorVerifyResponseBody() {
        SoftAssert softAssert = new SoftAssert();
        Player expectedPlayer = TestDataGenerator.generateValidPlayer();

        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), expectedPlayer);
        PlayerResponse actualPlayerResponseBody = actualPlayer.readEntity();

        actualPlayer.expectingStatusCode(200);
        //todo assert not list in rsponse , but object

        assertNotNull(actualPlayerResponseBody.toString(), "Response body should not be null");

        softAssert.assertEquals(actualPlayerResponseBody.getAge(), expectedPlayer.getAge(),
                "Age should be equal, but is not.");
        softAssert.assertEquals(actualPlayerResponseBody.getGender(), expectedPlayer.getGender(),
                "Gender should be equal, but is not.");
        softAssert.assertEquals(actualPlayerResponseBody.getLogin(), expectedPlayer.getLogin(),
                "Login should be equal, but is not.");
        softAssert.assertEquals(actualPlayerResponseBody.getPassword(), expectedPlayer.getPassword(),
                "Password should be equal, but is not.");
        softAssert.assertEquals(actualPlayerResponseBody.getRole(), expectedPlayer.getRole(),
                "Role should be equal, but is not.");
        softAssert.assertEquals(actualPlayerResponseBody.getScreenName(), expectedPlayer.getScreenName(),
                "ScreenName should be equal, but is not.");

        Integer playerId = actualPlayerResponseBody.getPlayerId();
        assertNotNull(playerId, "PlayerId should be not null");
        createdPlayerIds.add(playerId);

        softAssert.assertAll();
    }

    // те саме, але перевірити гетнувши юзера і перевірити чи засетані поля
    @Test(description = "Create player with valid data using supervisor")
    public void testCreatePlayerWithValidDataUsingSupervisor() {
        SoftAssert softAssert = new SoftAssert();
        Player expectedPlayer = TestDataGenerator.generateValidPlayer();

        ResponseWrapper<PlayerResponse> actualPlayer = apiClient
                .createPlayer(PropertiesReader.getProperty("default.supervisor.login"), expectedPlayer);
        PlayerResponse actualPlayerResponseBody = actualPlayer.readEntity();

        actualPlayer.expectingStatusCode(200);

        assertNotNull(actualPlayerResponseBody.toString(), "Response body should not be null");

        ResponseWrapper<PlayerResponse> getPlayerResponse = apiClient.getPlayer(actualPlayerResponseBody.getPlayerId());
        getPlayerResponse.expectingStatusCode(200);
        PlayerResponse getlayerResponseBody = getPlayerResponse.readEntity();


        softAssert.assertEquals(getlayerResponseBody.getAge(), expectedPlayer.getAge(),
                "Age should be equal, but is not.");
        softAssert.assertEquals(getlayerResponseBody.getGender(), expectedPlayer.getGender(),
                "Gender should be equal, but is not.");
        softAssert.assertEquals(getlayerResponseBody.getLogin(), expectedPlayer.getLogin(),
                "Login should be equal, but is not.");
        softAssert.assertEquals(getlayerResponseBody.getPassword(), expectedPlayer.getPassword(),
                "Password should be equal, but is not.");
        softAssert.assertEquals(getlayerResponseBody.getRole(), expectedPlayer.getRole(),
                "Role should be equal, but is not.");
        softAssert.assertEquals(getlayerResponseBody.getScreenName(), expectedPlayer.getScreenName(),
                "ScreenName should be equal, but is not.");

        Integer playerId = getlayerResponseBody.getPlayerId();
        assertNotNull(playerId, "PlayerId should be not null");
        createdPlayerIds.add(playerId);

        softAssert.assertAll();
    }
}
